package com.wintersky.windyreader.shelf;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.wintersky.windyreader.R;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeleteFragment extends DialogFragment {

    private static final String ARG_TIT = "title";
    private static final String ARG_URL = "url";

    @Inject ShelfContract.Presenter mPresenter;
    @BindView(R.id.title) TextView mTitle;
    Unbinder unbinder;

    private String bkTitle;
    private String bkUrl;

    public DeleteFragment() {
        // Required empty public constructor
    }

    public static DeleteFragment newInstance(String title, String url) {
        DeleteFragment fragment = new DeleteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TIT, title);
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bkTitle = getArguments().getString(ARG_TIT);
            bkUrl = getArguments().getString(ARG_URL);
        }
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        Activity activity = getActivity();
        Window window = getDialog().getWindow();
        if (activity != null && window != null) {
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.setLayout((int) (dm.widthPixels * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_delete, container, false);
        unbinder = ButterKnife.bind(this, view);
        mTitle.setText(bkTitle);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.delete, R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.delete:
                mPresenter.deleteBook(bkUrl);
            case R.id.cancel:
                dismiss();
                break;
        }
    }
}
