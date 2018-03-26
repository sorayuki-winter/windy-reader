package com.wintersky.windyreader.read;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wintersky.windyreader.R;
import com.wintersky.windyreader.data.Chapter;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReadFragment extends DaggerFragment implements ReadContract.View {

    @Inject
    ReadContract.Presenter mPresenter;

    private TextView tvContent;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_read, container, false);

        tvContent = view.findViewById(R.id.read_content);

        return view;
    }

    @Override
    public void setChapter(Chapter chapter) {
        tvContent.setText(chapter.content);
    }
}
