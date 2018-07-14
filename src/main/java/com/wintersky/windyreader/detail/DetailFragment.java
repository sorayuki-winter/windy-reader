package com.wintersky.windyreader.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.wintersky.windyreader.R;
import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.read.ReadActivity;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

import static com.wintersky.windyreader.detail.DetailActivity.BOOK_URL;
import static com.wintersky.windyreader.read.ReadActivity.CHAPTER_URL;
import static com.wintersky.windyreader.util.Constants.WS;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends DaggerFragment implements DetailContract.View {

    @Inject
    DetailContract.Presenter mPresenter;

    @Inject
    String bookUrl;
    @Inject
    ListAdapter mAdapter;
    private Button btCollect;

    @Inject
    public DetailFragment() {
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        btCollect = view.findViewById(R.id.collect);
        btCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.saveBook(bookUrl);
                btCollect.setText("已收藏");
            }
        });

        ListView lvChapters = view.findViewById(R.id.catalog);
        lvChapters.setAdapter(mAdapter);
        lvChapters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Activity activity = getActivity();
                if (activity != null) {
                    Intent intent = new Intent();
                    intent.setClass(activity, ReadActivity.class);
                    intent.putExtra(BOOK_URL, bookUrl);
                    intent.putExtra(CHAPTER_URL, mAdapter.getItem(position).getUrl());
                    startActivity(intent);
                } else {
                    WS("DetailFragment.lvChapters.onItemClick", "activity null");
                }
            }
        });

        return view;
    }

    @Override
    public void setBook(Book book) {
        btCollect.setEnabled(true);
    }

    @Override
    public void setChapters(List<Chapter> list) {
        mAdapter.setChapters(list);
    }

    static class ListAdapter extends BaseAdapter {
        List<Chapter> mList;

        private Context mContext;

        @Inject
        ListAdapter(Context mContext) {
            this.mContext = mContext;
        }

        void setChapters(List<Chapter> list) {
            mList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (mList != null)
                return mList.size();
            return 0;
        }

        @Override
        public Chapter getItem(int position) {
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

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_chapter, parent, false);
                holder.tv = convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv.setText(getItem(position).getTitle());

            return convertView;
        }

        class ViewHolder {
            TextView tv;
        }
    }
}
