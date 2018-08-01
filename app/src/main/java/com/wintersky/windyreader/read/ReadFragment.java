package com.wintersky.windyreader.read;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wintersky.windyreader.R;
import com.wintersky.windyreader.catalog.CatalogActivity;
import com.wintersky.windyreader.data.Chapter;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

import static android.app.Activity.RESULT_OK;
import static com.wintersky.windyreader.read.ReadActivity.CHAPTER_URL;
import static com.wintersky.windyreader.shelf.ShelfActivity.BOOK_URL;
import static com.wintersky.windyreader.util.Constants.WS;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReadFragment extends DaggerFragment implements ReadContract.View {

    private static final int REQUEST_CATALOG = 1;

    private static final int UI_ANIMATION_DELAY = 300;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private final Handler mHandler = new Handler();
    @Inject
    ReadContract.Presenter mPresenter;
    @Inject
    String mUrl;
    private View mTBar, mBBar;
    private final Runnable hideControl = new Runnable() {
        @Override
        public void run() {
            // Delayed removal of control bar
            mTBar.setVisibility(View.GONE);
            mBBar.setVisibility(View.GONE);
        }
    };
    private TextView mTitle;
    private ScrollView mScroll;
    private TextView mContent;
    private boolean mVisible = true;
    private final Runnable showSystem = new Runnable() {
        @Override
        public void run() {
            // Delayed removal of system UI
            mContent.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            mVisible = true;
        }
    };
    private final Runnable hideAll = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Inject
    public ReadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.dropView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CATALOG && resultCode == RESULT_OK) {
            String url = data.getStringExtra(CHAPTER_URL);
            mPresenter.loadChapter(url);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.takeView(this);
        if (!mVisible) {
            hide();
        }
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_read, container, false);

        mTBar = view.findViewById(R.id.top_bar);
        mBBar = view.findViewById(R.id.bottom_bar);
        mTitle = view.findViewById(R.id.title);
        mScroll = view.findViewById(R.id.scroll);
        mContent = view.findViewById(R.id.content);

        mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        view.findViewById(R.id.catalog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity != null) {
                    Intent intent = new Intent();
                    intent.setClass(activity, CatalogActivity.class);
                    intent.putExtra(BOOK_URL, mUrl);
                    startActivityForResult(intent, REQUEST_CATALOG);
                } else {
                    WS("showCatalog onClick", "Activity null");
                }
            }
        });

        view.findViewById(R.id.prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.prevChapter();
            }
        });
        view.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.nextChapter();
            }
        });

        hide();

        return view;
    }

    @Override
    public void setChapter(Chapter chapter) {
        mTitle.setText(chapter.getTitle());
        mContent.setText(chapter.getContent());
        mScroll.scrollTo(0, 0);
        hide();
    }

    private void hide() {
        // Hide the system UI first
        mContent.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        mVisible = false;

        // Schedule a runnable to remove the control bar after a delay
        mHandler.removeCallbacks(showSystem);
        mHandler.postDelayed(hideControl, UI_ANIMATION_DELAY);
    }

    private void show() {
        // Show the control bar first
        mTBar.setVisibility(View.VISIBLE);
        mBBar.setVisibility(View.VISIBLE);
        mVisible = true;

        // Schedule a runnable to display the system UI after a delay
        mHandler.removeCallbacks(hideControl);
        mHandler.post(showSystem);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide() {
        mHandler.removeCallbacks(hideAll);
        mHandler.postDelayed(hideAll, AUTO_HIDE_DELAY_MILLIS);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
            delayedHide();
        }
    }
}
