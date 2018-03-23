package com.wintersky.windyreader.shelf;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wintersky.windyreader.R;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShelfFragment extends DaggerFragment implements ShelfContract.View {

    @Inject
    ShelfContract.Presenter presenter;

    @Inject
    ShelfFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shelf, container, false);

        return view;
    }
}
