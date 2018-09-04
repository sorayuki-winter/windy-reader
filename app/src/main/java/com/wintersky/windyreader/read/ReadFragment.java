package com.wintersky.windyreader.read;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.wintersky.windyreader.R;
import com.wintersky.windyreader.catalog.CatalogActivity;
import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

import static android.app.Activity.RESULT_OK;
import static com.wintersky.windyreader.read.ReadActivity.CHAPTER_IDX;
import static com.wintersky.windyreader.shelf.ShelfActivity.BOOK_URL;
import static com.wintersky.windyreader.util.LogUtil.LOG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReadFragment extends DaggerFragment implements ReadContract.View {

    private static final int REQUEST_CATALOG = 1;

    @Inject
    ReadContract.Presenter mPresenter;
    @Inject
    String mBookUrl;

    private Book mBook;
    private boolean mVisible = true;
    private Point outSize;
    private float downX, downY;
    private boolean loadingPrev, loadingNext;

    private View mBottomBar;
    private ViewPager mViewPager;
    private PageAdapter mAdapter;
    private TextView mContent;

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
            int index = data.getIntExtra(CHAPTER_IDX, 0);
            if (index < mBook.catalog.size()) {
                mPresenter.saveReadIndex(index);
                mPresenter.loadContent(mBook.catalog.get(index), 0);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.takeView(this);
        hide();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_read, container, false);

        if (getActivity() != null) {
            outSize = new Point();
            WindowManager wm = getActivity().getWindowManager();
            wm.getDefaultDisplay().getSize(outSize);
        }

        mBottomBar = view.findViewById(R.id.bottom_bar);
        mViewPager = view.findViewById(R.id.view_pager);
        mContent = view.findViewById(R.id.content);

        mAdapter = new PageAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                PageAdapter.Page page = mAdapter.getPage(position);
                mPresenter.saveReadIndex(page.chapterIndex + (float) page.pageIndex / page.pageCount);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    turnCheck();
                    preLoad();
                }
            }
        });
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        break;

                    case MotionEvent.ACTION_UP:
                        float x = event.getX(), y = event.getY();
                        float dx = x - downX, dy = y - downY;
                        float xp = x / outSize.x, yp = y / outSize.y;
                        if (Math.abs(dx) < 20 && Math.abs(dy) < 20) {
                            if (mVisible) {
                                hide();
                            } else {
                                if (xp < 0.3 || xp < 0.7 && yp < 0.3) {
                                    turnCheck();
                                    int index = mViewPager.getCurrentItem() - 1;
                                    if (index >= 0) {
                                        mViewPager.setCurrentItem(index);
                                    }
                                } else if (xp > 0.7 || xp > 0.3 && yp > 0.7) {
                                    turnCheck();
                                    int index = mViewPager.getCurrentItem() + 1;
                                    if (index < mAdapter.getCount()) {
                                        mViewPager.setCurrentItem(index);
                                    }
                                } else {
                                    show();
                                }
                            }
                        }
                        break;
                }
                return mVisible;
            }
        });

        view.findViewById(R.id.catalog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity != null) {
                    Intent intent = new Intent();
                    intent.setClass(activity, CatalogActivity.class);
                    intent.putExtra(BOOK_URL, mBookUrl);
                    startActivityForResult(intent, REQUEST_CATALOG);
                } else {
                    LOG("showCatalog onClick", "Activity null");
                }
            }
        });

        view.findViewById(R.id.browser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = mAdapter.getPage(mViewPager.getCurrentItem()).chapterIndex;
                Chapter chapter = mBook.catalog.get(index);
                if (chapter != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(chapter.url));
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public void setBook(Book book) {
        mBook = book;
        if (book.index < book.catalog.size()) {
            mPresenter.loadContent(book.catalog.get((int) book.index), book.index % 1);
        }
    }

    @Override
    public void onBookCached() {
        Toast.makeText(getContext(), "cache finished", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setContent(Chapter chapter, String content, float progress) {
        mContent.setText(content);

        int count = mContent.getLineCount();
        int h = mContent.getBottom() - mContent.getTop() - mContent.getPaddingTop() - mContent.getPaddingBottom();
        int pCount = h / mContent.getLineHeight();
        int pageNum = count / pCount;
        int prev_end, end = 0;
        List<String> pageList = new ArrayList<>();
        for (int i = 0; i < pageNum; i++) {
            prev_end = end;
            end = mContent.getLayout().getLineEnd((i + 1) * pCount - 1);
            pageList.add(content.substring(prev_end, end));
        }
        pageList.add(content.substring(end));

        if (progress > 1) {
            int index = mViewPager.getCurrentItem();
            int addCount = mAdapter.prev(chapter, pageList);
            loadingPrev = false;
            mViewPager.setCurrentItem(index + addCount, false);
        } else if (progress < 0) {
            int index = mViewPager.getCurrentItem();
            int delCount = mAdapter.next(chapter, pageList);
            loadingNext = false;
            mViewPager.setCurrentItem(index - delCount, false);
        } else {
            mAdapter.set(chapter, pageList);
            int pageIndex = Math.round(mAdapter.getCount() * progress);
            if (pageIndex >= mAdapter.getCount()) {
                mViewPager.setCurrentItem(mAdapter.getCount() - 1, false);
            } else {
                mViewPager.setCurrentItem(pageIndex, false);
            }
            loadingPrev = loadingNext = false;
            preLoad();
        }
    }

    private void preLoad() {
        if (!loadingNext && mBook.index >= mAdapter.getNextIndex()) {
            int index = (int) mBook.index + 1;
            if (index < mBook.catalog.size()) {
                mPresenter.loadContent(mBook.catalog.get(index), -1);
                loadingNext = true;
            }
        }
        if (!loadingPrev && mBook.index - 1 < mAdapter.getPrevIndex()) {
            int index = (int) mBook.index - 1;
            if (index >= 0) {
                mPresenter.loadContent(mBook.catalog.get(index), 2);
                loadingPrev = true;
            }
        }
    }

    private void turnCheck() {
        if (mViewPager.getCurrentItem() == 0 || mViewPager.getCurrentItem() == mAdapter.getCount() - 1) {
            if (mBook.index < 1 || mBook.index >= mBook.catalog.size() - 1) {
                Toast.makeText(getContext(), "No More Content", Toast.LENGTH_SHORT).show();
            } else if (loadingNext || loadingPrev) {
                Toast.makeText(getContext(), "Wait Loading", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void hide() {
        mBottomBar.setVisibility(View.GONE);
        mVisible = false;
    }

    private void show() {
        mBottomBar.setVisibility(View.VISIBLE);
        mVisible = true;
    }
}
