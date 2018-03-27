package com.wintersky.windyreader.read;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wintersky.windyreader.R;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.detail.DetailActivity;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

import static com.wintersky.windyreader.detail.DetailActivity.BOOK_URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReadFragment extends DaggerFragment implements ReadContract.View {

    @Inject
    ReadContract.Presenter mPresenter;

    @Inject
    String[] urls;

    private TextView tvContent;

    private Button showCatalog;

    @Inject
    public ReadFragment() {
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_read, container, false);

        tvContent = view.findViewById(R.id.read_content);

        showCatalog = view.findViewById(R.id.read_show_list);
        showCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), DetailActivity.class);
                intent.putExtra(BOOK_URL, urls[1]);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void setChapter(Chapter chapter) {
        tvContent.setText(chapter.content);
    }
}
