package com.wintersky.windyreader.catalog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import dagger.android.support.DaggerFragment;
import io.realm.RealmBaseAdapter;
import io.realm.RealmList;

import static android.app.Activity.RESULT_OK;
import static com.wintersky.windyreader.read.ReadActivity.CHAPTER_IDX;
import static com.wintersky.windyreader.util.LogUtil.LOG;

/**
 * A simple {@link Fragment} subclass.
 */
public class CatalogFragment extends DaggerFragment implements CatalogContract.View {

    @Inject
    CatalogContract.Presenter mPresenter;
    @Inject
    String mUrl;

    private int mIndex;

    private TextView mTitle;
    private ListView mListView;
    private CatalogAdapter mAdapter;

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

        mTitle = view.findViewById(R.id.title);
        mListView = view.findViewById(R.id.catalog);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Activity activity = getActivity();
                Chapter chapter = mAdapter.getItem(position);
                if (activity != null && chapter != null) {
                    Intent intent = new Intent();
                    intent.putExtra(CHAPTER_IDX, chapter.index);
                    activity.setResult(RESULT_OK, intent);
                    activity.finish();
                } else {
                    LOG("CatalogFragment.mListView.onItemClick", "activity or chapter null");
                }
            }
        });

        return view;
    }

    @Override
    public void setBook(Book book) {
        mIndex = (int) book.index;

        mTitle.setText(book.getTitle());
        mAdapter = new CatalogAdapter(book.getCatalog());
        mListView.setAdapter(mAdapter);
        mListView.setSelection(mIndex);
    }

    class CatalogAdapter extends RealmBaseAdapter<Chapter> {

        CatalogAdapter(@Nullable RealmList<Chapter> data) {
            super(data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            Chapter chapter = getItem(position);

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_catalog, parent, false);

                holder.title = convertView.findViewById(R.id.title);
                holder.flag = convertView.findViewById(R.id.mark);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (chapter != null) {
                holder.title.setText(chapter.getTitle().trim());
                if (chapter.isRead()) {
                    holder.title.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.catalogRead));
                } else {
                    holder.title.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.catalogUnread));
                }
                if (chapter.index == mIndex) {
                    holder.flag.setVisibility(View.VISIBLE);
                } else {
                    holder.flag.setVisibility(View.INVISIBLE);
                }
            } else {
                holder.title.setText(null);
            }
            return convertView;
        }

        class ViewHolder {
            TextView title;
            View flag;
        }
    }
}
