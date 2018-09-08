package com.wintersky.windyreader.read;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;
import butterknife.OnTouch;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;

import static android.app.Activity.RESULT_OK;
import static com.wintersky.windyreader.read.ReadActivity.CHAPTER_IDX;
import static com.wintersky.windyreader.shelf.ShelfActivity.BOOK_URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReadFragment extends DaggerFragment implements ReadContract.View,
                                                            View.OnTouchListener,
                                                            ViewPager.OnPageChangeListener {

    private static final int REQUEST_CATALOG = 1;

    @Inject ReadContract.Presenter mPresenter;
    @Inject ReadAdapter mAdapter;
    Unbinder unbinder;
    @BindView(R.id.view_pager) ViewPager mViewPager;
    @BindView(R.id.bottom_bar) ConstraintLayout mBottomBar;
    @BindView(R.id.content) TextView mContent;
    @BindView(R.id.top_bar) ConstraintLayout mTopBar;

    private Book mBook;
    private boolean mVisible = true;
    private Point outSize;
    private float downX, downY;
    private boolean loadingPrev, loadingNext;

    @Inject
    public ReadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.takeView(this);
        hide();
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
                mPresenter.getContent(mBook.catalog.get(index), 0);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_read, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (getActivity() != null) {
            outSize = new Point();
            WindowManager wm = getActivity().getWindowManager();
            wm.getDefaultDisplay().getSize(outSize);
        }
        mViewPager.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void setBook(Book book) {
        mBook = book;
        if (book.index < book.catalog.size()) {
            mPresenter.getContent(book.catalog.get((int) book.index), book.index % 1);
        }
    }

    @Override
    public void setContent(Chapter chapter, String content, float progress) {
        mContent.setText(content);

        List<String> pageList = new ArrayList<>();
        if (content == null) {
            pageList.add("(NULL)");
        } else if (content.isEmpty()) {
            pageList.add("(EMPTY)");
        } else {
            int count = mContent.getLineCount();
            int h = mContent.getBottom() - mContent.getTop() - mContent.getPaddingTop() - mContent.getPaddingBottom();
            int pCount = h / mContent.getLineHeight();
            int pageNum = count / pCount;
            int prev_end, end = 0;
            for (int i = 0; i < pageNum; i++) {
                prev_end = end;
                end = mContent.getLayout().getLineEnd((i + 1) * pCount - 1);
                pageList.add(content.substring(prev_end, end));
            }
            if (end < content.length()) {
                pageList.add(content.substring(end));
            }
        }

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
                mPresenter.getContent(mBook.catalog.get(index), -1);
                loadingNext = true;
            }
        }
        if (!loadingPrev && mBook.index - 1 < mAdapter.getPrevIndex()) {
            int index = (int) mBook.index - 1;
            if (index >= 0) {
                mPresenter.getContent(mBook.catalog.get(index), 2);
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
        mTopBar.setVisibility(View.GONE);
        mBottomBar.setVisibility(View.GONE);
        mVisible = false;
    }

    private void show() {
        mTopBar.setVisibility(View.VISIBLE);
        mBottomBar.setVisibility(View.VISIBLE);
        mVisible = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.catalog, R.id.browser})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.catalog:
                if (getActivity() != null) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), CatalogActivity.class);
                    intent.putExtra(BOOK_URL, mBook.url);
                    startActivityForResult(intent, REQUEST_CATALOG);
                }
                break;
            case R.id.browser:
                int index = mAdapter.getPage(mViewPager.getCurrentItem()).chapterIndex;
                Chapter chapter = mBook.catalog.get(index);
                if (chapter != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(chapter.url));
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

    }

    @OnPageChange(R.id.view_pager)
    @Override
    public void onPageSelected(int position) {
        ReadAdapter.Page page = mAdapter.getPage(position);
        mPresenter.saveReadIndex(page.chapterIndex + (float) page.index / page.count);
    }

    @OnPageChange(value = R.id.view_pager, callback = OnPageChange.Callback.PAGE_SCROLL_STATE_CHANGED)
    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            turnCheck();
            preLoad();
        }
    }

    @OnTouch(R.id.view_pager)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
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

    @OnClick(R.id.back)
    public void onViewClicked() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }
}
