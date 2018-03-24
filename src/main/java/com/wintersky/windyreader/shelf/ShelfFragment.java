package com.wintersky.windyreader.shelf;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.wintersky.windyreader.R;
import com.wintersky.windyreader.data.Book;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShelfFragment extends DaggerFragment implements ShelfContract.View {

    @Inject
    ShelfContract.Presenter mPresenter;

    private GridView gridView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shelf, container, false);

        gridView = view.findViewById(R.id.shelf_gv);
        gridView.setAdapter(adapter);

        return view;
    }

    @Override
    public void setBooks(List<Book> list) {
        adapter.setBookList(list);
    }

    static class GridAdapter extends BaseAdapter {

        private List<Book> bookList;

        private Context context;

        @Inject
        GridAdapter(Context context){
            this.context = context;
        }

        public void setBookList(List<Book> list) {
            this.bookList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if(bookList != null)
                return bookList.size();
            return 0;
        }

        @Override
        public Book getItem(int position) {
            if (bookList != null)
                return bookList.get(position);
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;

            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(this.context).inflate(R.layout.item_shelf, viewGroup, false);
                viewHolder.tv = view.findViewById(R.id.shelf_it_name_txt);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.tv.setText(getItem(i).title);

            return view;
        }

        class ViewHolder {
            TextView tv;
        }
    }
}
