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

import dagger.android.support.AndroidSupportInjection;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeleteFragment extends DialogFragment {

    @Inject
    ShelfContract.Presenter mPresenter;

    private String mUrl;
    private String mTitle;

    @Inject
    public DeleteFragment() {
        // Required empty public constructor
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (args == null) {
            mUrl = null;
            mTitle = null;
        } else {
            mUrl = args.getString("url");
            mTitle = args.getString("title");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
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

        TextView title = view.findViewById(R.id.title);

        title.setText(mTitle);

        view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.deleteBook(mUrl);
                dismiss();
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}
