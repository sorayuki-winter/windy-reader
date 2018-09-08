package com.wintersky.windyreader.catalog;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.wintersky.windyreader.R;
import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;

import static android.app.Activity.RESULT_OK;
import static com.wintersky.windyreader.read.ReadActivity.CHAPTER_IDX;
import static com.wintersky.windyreader.util.LogUtil.LOG;

/**
 * A simple {@link Fragment} subclass.
 */
public class CatalogFragment extends DaggerFragment implements CatalogContract.View,
                                                               AdapterView.OnItemClickListener {

    @Inject CatalogContract.Presenter mPresenter;
    @Inject CatalogAdapter mAdapter;
    Unbinder unbinder;
    @BindView(R.id.catalog) ListView mCatalog;
    @BindView(R.id.title) TextView mTitle;
    @BindView(R.id.is_loading) TextView mIsLoading;

    private View mHeader, mFooter;

    @Inject
    public CatalogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.takeView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.dropView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);
        unbinder = ButterKnife.bind(this, view);
        mCatalog.setAdapter(mAdapter);
        mHeader = View.inflate(getContext(), R.layout.header_catalog, null);
        mFooter = View.inflate(getContext(), R.layout.footer_catalog, null);
        mCatalog.addHeaderView(mHeader);
        mCatalog.addFooterView(mFooter);
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                if (mAdapter.getCount() > 0) {
                    mHeader.findViewById(R.id.no_more).setVisibility(View.VISIBLE);
                    mFooter.findViewById(R.id.no_more).setVisibility(View.VISIBLE);
                } else {
                    mHeader.findViewById(R.id.no_more).setVisibility(View.GONE);
                    mFooter.findViewById(R.id.no_more).setVisibility(View.GONE);
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setBook(Book book) {
        mTitle.setText(book.getTitle());
        final int index = (int) book.index;
        mAdapter.updateData(index, book.getCatalog());
        mCatalog.post(new Runnable() {
            @Override
            public void run() {
                mCatalog.setSelectionFromTop(index, mCatalog.getHeight() / 3);
            }
        });
        mIsLoading.setVisibility(View.GONE);
        mCatalog.setVisibility(View.VISIBLE);
    }

    @OnItemClick(R.id.catalog)
    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        Activity activity = getActivity();
        Chapter chapter = mAdapter.getItem(position - 1);
        if (activity != null && chapter != null) {
            Intent intent = new Intent();
            intent.putExtra(CHAPTER_IDX, chapter.index);
            activity.setResult(RESULT_OK, intent);
            activity.finish();
        } else {
            LOG("CatalogFragment.mListView.onItemClick", "activity or chapter null");
        }
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }
}
