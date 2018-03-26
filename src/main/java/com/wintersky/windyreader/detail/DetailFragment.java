package com.wintersky.windyreader.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.wintersky.windyreader.R;
import com.wintersky.windyreader.data.Chapter;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends DaggerFragment implements DetailContract.View {

    @Inject
    DetailContract.Presenter mPresenter;

    private Button btCollect;

    private ListView lvChapters;

    @Inject
    ListAdapter mAdapter;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        btCollect = view.findViewById(R.id.detail_collect);
        lvChapters = view.findViewById(R.id.detail_chapters);

        lvChapters.setAdapter(mAdapter);

        btCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
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

        public void setChapters(List<Chapter> list) {
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

            if(convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_chapter, parent, false);
                holder.tv = convertView.findViewById(R.id.detail_item_title);
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
