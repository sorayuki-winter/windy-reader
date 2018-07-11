package com.wintersky.windyreader.read;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wintersky.windyreader.R;
import com.wintersky.windyreader.catalog.CatalogActivity;
import com.wintersky.windyreader.data.Chapter;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

import static android.app.Activity.RESULT_OK;
import static com.wintersky.windyreader.detail.DetailActivity.BOOK_URL;
import static com.wintersky.windyreader.read.ReadActivity.CHAPTER_URL;
import static com.wintersky.windyreader.util.Constants.WS;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReadFragment extends DaggerFragment implements ReadContract.View {

    private static final int REQUEST_CATALOG = 1;

    private static final int UI_ANIMATION_DELAY = 300;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    @Inject
    ReadContract.Presenter mPresenter;
    @Inject
    String[] mUrls;

    private View mTBar, mBBar;
    private TextView tvTitle;
    private ScrollView mScroll;
    private TextView tvContent;
    private boolean mVisible = true;
    private Handler mHandler = new Handler();
    private Runnable runShow2 = new Runnable() {
        @Override
        public void run() {
            mTBar.setVisibility(View.VISIBLE);
            mBBar.setVisibility(View.VISIBLE);
        }
    };
    private Runnable runHide2 = new Runnable() {
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            tvContent.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private Runnable runHide = new Runnable() {
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
    public void onResume() {
        super.onResume();
        if (!mVisible) {
            hide();
        }
        mPresenter.takeView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.dropView();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_read, container, false);

        mTBar = view.findViewById(R.id.top_bar);
        mBBar = view.findViewById(R.id.bottom_bar);
        tvTitle = view.findViewById(R.id.title);
        mScroll = view.findViewById(R.id.scroll);
        tvContent = view.findViewById(R.id.content);

        //mTBar.bringToFront();
        //mBBar.bringToFront();

        tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        Button showCatalog = view.findViewById(R.id.catalog);
        showCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity != null) {
                    Intent intent = new Intent();
                    intent.setClass(activity, CatalogActivity.class);
                    intent.putExtra(BOOK_URL, mUrls[1]);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CATALOG && resultCode == RESULT_OK) {
            String url = data.getStringExtra(CHAPTER_URL);
            mPresenter.loadChapter(url);
        }
    }

    @Override
    public void setChapter(Chapter chapter) {
        tvTitle.setText(chapter.getTitle());
        tvContent.setText(chapter.getContent());
        mScroll.scrollTo(0, 0);
        hide();
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
            delayedHide(AUTO_HIDE_DELAY_MILLIS);
        }
    }

    private void hide() {
        // Hide UI first
        mTBar.setVisibility(View.GONE);
        mBBar.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHandler.removeCallbacks(runShow2);
        mHandler.postDelayed(runHide2, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        tvContent.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHandler.removeCallbacks(runHide2);
        mHandler.post(runShow2);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHandler.removeCallbacks(runHide);
        mHandler.postDelayed(runHide, delayMillis);
    }

}
