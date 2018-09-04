package com.wintersky.windyreader.read;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.wintersky.windyreader.data.Chapter;

import java.util.ArrayList;
import java.util.List;

public class PageAdapter extends FragmentStatePagerAdapter {

    private List<Page> mList;

    public PageAdapter(FragmentManager fm) {
        super(fm);
    }

    public void set(Chapter chapter, List<String> list) {
        mList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Page page = new Page(chapter.index, i, list.size(), chapter.title, list.get(i));
            mList.add(page);
        }
        notifyDataSetChanged();
    }

    public int prev(Chapter chapter, List<String> list) {
        if (chapter.index != getPrevIndex() - 1) {
            return 0;
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            Page page = new Page(chapter.index, i, list.size(), chapter.title, list.get(i));
            mList.add(0, page);
        }
        while (getNextIndex() - getPrevIndex() > 2) {
            mList.remove(mList.size() - 1);
        }
        notifyDataSetChanged();
        return list.size();
    }

    public int next(Chapter chapter, List<String> list) {
        if (chapter.index != getNextIndex() + 1) {
            return 0;
        }
        for (int i = 0; i < list.size(); i++) {
            Page page = new Page(chapter.index, i, list.size(), chapter.title, list.get(i));
            mList.add(page);
        }
        int delCount = 0;
        while (getNextIndex() - getPrevIndex() > 2) {
            mList.remove(0);
            delCount++;
        }
        notifyDataSetChanged();
        return delCount;
    }

    public int getPrevIndex() {
        return mList.get(0).chapterIndex;
    }

    public int getNextIndex() {
        return mList.get(mList.size() - 1).chapterIndex;
    }

    public Page getPage(int position) {
        return mList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return new PageFragment();
    }

    @Override
    public int getCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PageFragment fragment = (PageFragment) super.instantiateItem(container, position);
        fragment.setPage(mList.get(position));
        return fragment;
    }

    public static class Page {
        public int chapterIndex;
        public int pageIndex;
        public int pageCount;
        public String title;
        public String content;

        public Page(int chapterIndex, int pageIndex, int pageCount, String title, String content) {
            this.chapterIndex = chapterIndex;
            this.pageIndex = pageIndex;
            this.pageCount = pageCount;
            this.title = title;
            this.content = content;
        }
    }
}
