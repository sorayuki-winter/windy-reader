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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wintersky.windyreader.R;
import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.read.ReadActivity;
import com.wintersky.windyreader.util.KeyboardUtil;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

import static com.wintersky.windyreader.shelf.ShelfActivity.BOOK_TIT;
import static com.wintersky.windyreader.shelf.ShelfActivity.BOOK_URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShelfFragment extends DaggerFragment implements ShelfContract.View {

    @Inject
    ShelfContract.Presenter mPresenter;

    private EditText mLink;
    private ImageButton mAdd;
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

        mLink = view.findViewById(R.id.link);
        mAdd = view.findViewById(R.id.add);
        mGridView = view.findViewById(R.id.book_grid);

        mLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mAdd.setEnabled(true);
                } else {
                    mAdd.setEnabled(false);
                }
            }
        });

        mAdd.setEnabled(false);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = mLink.getText().toString();
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    mLink.setError("URL should start with \"http(s)://\"");
                    mAdd.setEnabled(false);
                    return;
                }
                mPresenter.saveBook(url);
                KeyboardUtil.hideKeyboard(mLink);
            }
        });

        return view;
    }

    @Override
    public void setShelf(RealmResults<Book> list) {
        GridAdapter adapter = new GridAdapter(list);
        mGridView.setAdapter(adapter);
    }

    @Override
    public void onBookSaved(boolean ok) {
        if (ok) {
            mLink.getText().clear();
        } else {
            mLink.setError("Error");
            KeyboardUtil.showKeyboard(mLink);
        }
        mAdd.setEnabled(false);
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
                        DeleteFragment fragment = new DeleteFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(BOOK_URL, book.url);
                        bundle.putString(BOOK_TIT, book.title);
                        fragment.setArguments(bundle);
                        fragment.show(getChildFragmentManager(), DeleteFragment.TAG);
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
