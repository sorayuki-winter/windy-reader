package com.wintersky.windyreader.shelf;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
            }
        });

        mLink.getText().clear();

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
            final Book bk = getItem(i);

            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_shelf, viewGroup, false);
                holder.img = view.findViewById(R.id.cover);
                holder.tv = view.findViewById(R.id.title);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (bk == null) {
                holder.img.setImageDrawable(null);
                holder.tv.setText(null);
            } else {
                holder.img.setImageResource(R.mipmap.cover);
                holder.tv.setText(bk.getTitle());

                holder.img.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            Drawable drawable = ((ImageView) v).getDrawable();
                            int color = ContextCompat.getColor(viewGroup.getContext(), R.color.shelfCoverPress);
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
                        Intent intent = new Intent(viewGroup.getContext(), ReadActivity.class);
                        intent.putExtra(BOOK_URL, bk.getUrl());
                        viewGroup.getContext().startActivity(intent);
                    }
                });
                holder.img.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        DeleteFragment fragment = new DeleteFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("title", bk.getTitle());
                        bundle.putString("url", bk.getUrl());
                        fragment.setArguments(bundle);
                        if (getFragmentManager() != null) {
                            fragment.show(getFragmentManager(), "delete");
                        }
                        return true;
                    }
                });
            }
            return view;
        }

        class ViewHolder {
            ImageView img;
            TextView tv;
        }
    }
}
