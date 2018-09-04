package com.wintersky.windyreader.read;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wintersky.windyreader.R;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageFragment extends Fragment {

    private PageAdapter.Page mPage;

    public PageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_page, container, false);

        TextView title = view.findViewById(R.id.title);
        TextView content = view.findViewById(R.id.content);
        TextView index = view.findViewById(R.id.index);

        if (mPage != null) {
            title.setText(mPage.title);
            content.setText(mPage.content);
            index.setText(String.format(Locale.CHINA, "%d/%d", mPage.pageIndex + 1, mPage.pageCount));
        }

        return view;
    }

    public PageAdapter.Page getPage() {
        return mPage;
    }

    public void setPage(PageAdapter.Page page) {
        mPage = page;
    }
}
