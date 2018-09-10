package com.wintersky.windyreader.shelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.wintersky.windyreader.R;
import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.util.ErrorFragment;
import com.wintersky.windyreader.util.KeyboardUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShelfFragment extends DaggerFragment implements ShelfContract.View,
                                                             View.OnFocusChangeListener,
                                                             TextView.OnEditorActionListener {
    @Inject ShelfContract.Presenter mPresenter;
    @Inject ShelfAdapter mAdapter;
    Unbinder unbinder;
    @BindView(R.id.grid_view) GridView mGridView;
    @BindView(R.id.back) ImageButton mBack;
    @BindView(R.id.link) EditText mLink;
    @BindView(R.id.add) Button mAdd;
    @BindView(R.id.user) ImageButton mUser;

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
        unbinder = ButterKnife.bind(this, view);
        mGridView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setShelf(RealmResults<Book> list) {
        mAdapter.updateData(list);
    }

    @Override
    public void onBookSaved(Book book) {
        Toast.makeText(getContext(), "New Book:\n" + book.getTitle(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBookSaved(String url, Throwable error) {
        ErrorFragment fragment = ErrorFragment.newInstance("Book Add Fail:",
                String.format("%s\n%s", url, error.toString()));
        fragment.show(getChildFragmentManager(), "book_add_fail");
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

    @OnClick({R.id.back, R.id.add, R.id.user})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                mLink.clearFocus();
                break;
            case R.id.add:
                Toast.makeText(getContext(), "Do Nothing on Add", Toast.LENGTH_LONG).show();
                mLink.getEditableText().clear();
                mLink.clearFocus();
                break;
            case R.id.user:
                Toast.makeText(getContext(), "Do Nothing on User", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @OnFocusChange(R.id.link)
    @Override
    public void onFocusChange(final View v, final boolean hasFocus) {
        if (hasFocus) {
            KeyboardUtil.showKeyboard(mLink);
            mBack.setVisibility(View.VISIBLE);
            mAdd.setVisibility(View.VISIBLE);
            mUser.setVisibility(View.GONE);
            mGridView.setVisibility(View.GONE);
        } else {
            KeyboardUtil.hideKeyboard(mLink);
            mBack.setVisibility(View.GONE);
            mAdd.setVisibility(View.GONE);
            mUser.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.VISIBLE);
        }
    }

    public boolean onBackPressed() {
        if (mLink.isFocused()) {
            mLink.clearFocus();
            return true;
        }
        return false;
    }

    @OnEditorAction(R.id.link)
    @Override
    public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            mAdd.callOnClick();
        }
        return false;
    }
}
