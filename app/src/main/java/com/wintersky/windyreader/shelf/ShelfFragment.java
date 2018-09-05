package com.wintersky.windyreader.shelf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wintersky.windyreader.R;
import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.read.ReadActivity;
import com.wintersky.windyreader.util.ErrorFragment;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

import static com.wintersky.windyreader.shelf.ShelfActivity.BOOK_URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShelfFragment extends DaggerFragment implements ShelfContract.View {

    @Inject
    ShelfContract.Presenter mPresenter;

    private GridView mGridView;

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
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shelf, container, false);

        mGridView = view.findViewById(R.id.book_grid);

        return view;
    }

    public void onNewIntent(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();
        if (action != null && action.equals(Intent.ACTION_SEND)) {
            if (type != null && type.equals("text/plain")) {
                String url = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (url.matches("https?://.*")) {
                    mPresenter.saveBook(url);
                } else {
                    Toast.makeText(getContext(), "Bad Share: " + url, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void setShelf(RealmResults<Book> list) {
        GridAdapter adapter = new GridAdapter(list);
        mGridView.setAdapter(adapter);
    }

    @Override
    public void onBookSaved(Book book) {
        Toast.makeText(getContext(), "New Book:\n" + book.title, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBookSaved(String url, Exception e) {
        ErrorFragment fragment = ErrorFragment.newInstance("Book Add Fail:", String.format("%s\n%s", url, e.toString()));
        fragment.show(getChildFragmentManager(), "book_add_fail");
    }

    class GridAdapter extends RealmBaseAdapter<Book> {

        GridAdapter(@Nullable OrderedRealmCollection<Book> data) {
            super(data);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public View getView(int i, View view, final ViewGroup viewGroup) {
            ViewHolder holder;
            final Book book = getItem(i);

            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_shelf, viewGroup, false);
                holder.cover = view.findViewById(R.id.cover);
                holder.title = view.findViewById(R.id.title);
                holder.hasNew = view.findViewById(R.id.has_new);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (book == null) {
                holder.cover.setImageDrawable(null);
                holder.title.setText(null);
                holder.hasNew.setVisibility(View.INVISIBLE);
            } else {
                holder.cover.setImageResource(R.mipmap.cover);
                holder.title.setText(book.getTitle());
                if (book.isHasNew()) {
                    holder.hasNew.setVisibility(View.VISIBLE);
                } else {
                    holder.hasNew.setVisibility(View.INVISIBLE);
                }
                holder.cover.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                int color = ContextCompat.getColor(viewGroup.getContext(), R.color.shelfCoverPress);
                                ((ImageView) v).setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                                break;

                            case MotionEvent.ACTION_UP:
                            case MotionEvent.ACTION_CANCEL:
                                ((ImageView) v).clearColorFilter();
                                break;
                        }
                        return false;
                    }
                });
                holder.cover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Activity activity = getActivity();
                        if (activity != null) {
                            Intent intent = new Intent(activity, ReadActivity.class);
                            intent.putExtra(BOOK_URL, book.url);
                            activity.startActivity(intent);
                        }
                    }
                });
                holder.cover.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        DeleteFragment fragment = DeleteFragment.newInstance(book.title, book.url);
                        fragment.show(getChildFragmentManager(), "book_delete");
                        return true;
                    }
                });
            }
            return view;
        }

        class ViewHolder {
            ImageView cover;
            TextView title;
            ImageView hasNew;
        }
    }
}
