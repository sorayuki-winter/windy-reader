package com.wintersky.windyreader.read;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wintersky.windyreader.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageFragment extends Fragment {

    private static final String ARG_TIT = "title";
    private static final String ARG_CON = "content";
    private static final String ARG_IDX = "index";
    @BindView(R.id.title) TextView mTitle;
    @BindView(R.id.content) TextView mContent;
    @BindView(R.id.progress) TextView mProgress;
    Unbinder unbinder;

    private String pgTitle;
    private String pgContent;
    private String pgProgress;

    public PageFragment() {
        // Required empty public constructor
    }

    public static PageFragment newInstance(String title, String content, String index) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TIT, title);
        args.putString(ARG_CON, content);
        args.putString(ARG_IDX, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pgTitle = getArguments().getString(ARG_TIT);
            pgContent = getArguments().getString(ARG_CON);
            pgProgress = getArguments().getString(ARG_IDX);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        unbinder = ButterKnife.bind(this, view);
        mTitle.setText(pgTitle);
        mContent.setText(pgContent);
        mProgress.setText(pgProgress);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
