package com.wintersky.windyreader.util;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wintersky.windyreader.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ErrorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ErrorFragment extends DialogFragment {

    private static final String ARG_TIT = "title";
    private static final String ARG_CON = "content";

    private String mTitle;
    private String mContent;


    public ErrorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title   Parameter 1.
     * @param content Parameter 2.
     * @return A new instance of fragment ErrorFragment.
     */
    public static ErrorFragment newInstance(String title, String content) {
        ErrorFragment fragment = new ErrorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TIT, title);
        args.putString(ARG_CON, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TIT);
            mContent = getArguments().getString(ARG_CON);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_error, container, false);

        TextView title = view.findViewById(R.id.title);
        TextView content = view.findViewById(R.id.content);

        title.setText(mTitle);
        content.setText(mContent);

        return view;
    }
}
