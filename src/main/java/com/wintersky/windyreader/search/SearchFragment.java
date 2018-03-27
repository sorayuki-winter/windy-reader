package com.wintersky.windyreader.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.wintersky.windyreader.R;
import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.detail.DetailActivity;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

import static com.wintersky.windyreader.detail.DetailActivity.BOOK_URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends DaggerFragment implements SearchContract.View {

    @Inject
    SearchContract.Presenter mPresenter;

    private EditText searchKey;

    private ImageButton searchGo;

    private ListView searchResult;

    @Inject
    ListAdapter adapter;

    @Inject
    public SearchFragment() {
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchKey = view.findViewById(R.id.search_keyword);

        searchGo = view.findViewById(R.id.search_go);
        searchGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = searchKey.getText().toString();
                mPresenter.search("http://www.8wenku.com/site/search", key);
            }
        });

        searchResult = view.findViewById(R.id.search_result);
        searchResult.setAdapter(adapter);
        searchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getContext(), DetailActivity.class);
                intent.putExtra(BOOK_URL, adapter.getItem(position).getUrl());
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void setResult(List<Book> list) {
        adapter.setResult(list);
    }

    static class ListAdapter extends BaseAdapter {
        private List<Book> mList;

        private Context mContext;

        @Inject
        ListAdapter(Context mContext) {
            this.mContext = mContext;
        }

        void setResult(List<Book> list) {
            mList = list;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if(convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_search, parent, false);
                holder.tv = convertView.findViewById(R.id.search_item_title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv.setText(getItem(position).title);

            return convertView;
        }

        class ViewHolder {
            TextView tv;
        }
    }
}
