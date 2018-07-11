package com.wintersky.windyreader.shelf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.wintersky.windyreader.R;
import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.read.ReadActivity;
import com.wintersky.windyreader.search.SearchActivity;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

import static com.wintersky.windyreader.detail.DetailActivity.BOOK_URL;
import static com.wintersky.windyreader.read.ReadActivity.CHAPTER_URL;
import static com.wintersky.windyreader.util.Constants.WS;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShelfFragment extends DaggerFragment implements ShelfContract.View {

    @Inject
    ShelfContract.Presenter mPresenter;
    @Inject
    GridAdapter adapter;

    @Inject
    public ShelfFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.takeView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.dropView();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shelf, container, false);

        Button toSearch = view.findViewById(R.id.shelf_to_search);
        toSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                if (context != null) {
                    Intent intent = new Intent();
                    intent.setClass(getContext(), SearchActivity.class);
                    startActivity(intent);
                } else {
                    WS("Search onClick", "Context null");
                }
            }
        });

        GridView gridView = view.findViewById(R.id.shelf_gv);
        gridView.setAdapter(adapter);
        adapter.bind(getActivity());

        return view;
    }

    @Override
    public void setBooks(List<Book> list) {
        adapter.setBookList(list);
    }

    static class GridAdapter extends BaseAdapter {

        private List<Book> mList;

        private Context mContext;

        private Activity mActivity;

        @Inject
        GridAdapter(Context context) {
            mContext = context;
        }

        void bind(Activity activity) {
            mActivity = activity;
        }

        void setBookList(List<Book> list) {
            this.mList = list;
            for (int i = 0; i < list.size() % 3; i++) {
                Book book = new Book();
                list.add(book);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (mList == null)
                return 0;
            return mList.size();
        }

        @Override
        public Book getItem(int position) {
            if (mList != null && mList.size() > position)
                return mList.get(position);
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            final Book bk = getItem(i);

            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_shelf, viewGroup, false);
                holder.img = view.findViewById(R.id.cover);
                holder.tv = view.findViewById(R.id.shelf_it_name_txt);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            if (bk.url == null) {
                holder.img.setImageDrawable(null);
            } else {
                holder.img.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            Drawable drawable = ((ImageView) v).getDrawable();
                            int color = mContext.getResources().getColor(R.color.shelfCoverPress);
                            drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                            Drawable drawable = ((ImageView) v).getDrawable();
                            drawable.clearColorFilter();
                        }
                        return false;
                    }
                });
                holder.img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, ReadActivity.class);
                        intent.putExtra(BOOK_URL, bk.url);
                        intent.putExtra(CHAPTER_URL, bk.getCurrentCUrl());
                        mActivity.startActivity(intent);
                    }
                });
                holder.tv.setText(bk.title);
            }

            return view;
        }

        class ViewHolder {
            ImageView img;
            TextView tv;
        }
    }
}
