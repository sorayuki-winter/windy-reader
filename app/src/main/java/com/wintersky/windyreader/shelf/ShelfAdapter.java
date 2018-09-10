package com.wintersky.windyreader.shelf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wintersky.windyreader.R;
import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.read.ReadActivity;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTouch;
import io.realm.RealmBaseAdapter;

import static com.wintersky.windyreader.shelf.ShelfActivity.BOOK_URL;

public class ShelfAdapter extends RealmBaseAdapter<Book> {

    private final Activity mActivity;
    private final FragmentManager mFragmentManager;

    @Inject
    ShelfAdapter(ShelfActivity activity, FragmentManager manager) {
        super(null);
        mActivity = activity;
        mFragmentManager = manager;
    }

    @Override
    public View getView(int i, View view, final ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_shelf, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.setBook(getItem(i));
        return view;
    }

    class ViewHolder implements View.OnLongClickListener, View.OnTouchListener {
        @BindView(R.id.cover) ImageView cover;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.has_new) ImageView hasNew;
        @BindColor(R.color.shelfCoverPress) int coverPressed;

        private Book mBook;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void setBook(Book book) {
            mBook = book;
            if (book != null) {
                cover.setImageResource(R.mipmap.ic_book_cover);
                title.setText(book.getTitle());
                hasNew.setVisibility(book.isHasNew() ? View.VISIBLE : View.INVISIBLE);
            } else {
                cover.setImageDrawable(null);
                title.setText(null);
                hasNew.setVisibility(View.INVISIBLE);
            }
        }

        @OnClick(R.id.cover)
        public void onClick() {
            if (mActivity != null) {
                Intent intent = new Intent(mActivity, ReadActivity.class);
                intent.putExtra(BOOK_URL, mBook.getUrl());
                mActivity.startActivity(intent);
            }
        }

        @OnLongClick(R.id.cover)
        @Override
        public boolean onLongClick(final View v) {
            DeleteFragment fragment = DeleteFragment.newInstance(mBook.getTitle(), mBook.getUrl());
            fragment.show(mFragmentManager, "book_delete");
            return true;
        }

        @OnTouch(R.id.cover)
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(final View v, final MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    cover.setColorFilter(coverPressed, PorterDuff.Mode.MULTIPLY);
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    cover.clearColorFilter();
                    break;
            }
            return false;
        }
    }
}
