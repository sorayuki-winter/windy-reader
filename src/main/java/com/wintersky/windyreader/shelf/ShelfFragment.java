package com.wintersky.windyreader.shelf;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ShelfFragment extends DaggerFragment implements ShelfContract.View {

    @Inject
    ShelfContract.Presenter mPresenter;

    private Button toSearch;

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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shelf, container, false);

        toSearch = view.findViewById(R.id.shelf_to_search);
        toSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        gridView = view.findViewById(R.id.shelf_gv);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getContext(), ReadActivity.class);
                Book bk = adapter.getItem(position);
                intent.putExtra(BOOK_URL, bk.url);
                intent.putExtra(CHAPTER_URL,
                        bk.getChapterList().get(bk.lastRead).getUrl());
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void setBooks(List<Book> list) {
        adapter.setBookList(list);
    }

    static class GridAdapter extends BaseAdapter {

        private List<Book> mList;

        private Context mContext;

        @Inject
        GridAdapter(Context context){
            mContext = context;
        }

        void setBookList(List<Book> list) {
            this.mList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if(mList != null)
                return mList.size();
            return 0;
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

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;

            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_shelf, viewGroup, false);
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
