package com.wintersky.windyreader.catalog;

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
import android.widget.ListView;
import android.widget.TextView;

import com.wintersky.windyreader.R;
import com.wintersky.windyreader.data.Chapter;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

import static android.app.Activity.RESULT_OK;
import static com.wintersky.windyreader.read.ReadActivity.CHAPTER_URL;
import static com.wintersky.windyreader.util.Constants.WS;

/**
 * A simple {@link Fragment} subclass.
 */
public class CatalogFragment extends DaggerFragment implements CatalogContract.View {

    @Inject
    CatalogContract.Presenter mPresenter;
    @Inject
    CatalogAdapter mAdapter;
    @Inject
    String mUrl;

    private ListView mListView;

    @Inject
    public CatalogFragment() {
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
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);

        mListView = view.findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Activity activity = getActivity();
                if (activity != null) {
                    Intent intent = new Intent();
                    intent.putExtra(CHAPTER_URL, mAdapter.getItem(position).getUrl());
                    activity.setResult(RESULT_OK, intent);
                    activity.finish();
                } else {
                    WS("CatalogFragment.mListView.onItemClick", "activity null");
                }
            }
        });

        return view;
    }

    @Override
    public void setChapterList(List<Chapter> list) {
        mAdapter.setList(list);
        mListView.setSelection(85);
    }

    static class CatalogAdapter extends BaseAdapter {

        private List<Chapter> mList;

        private Context mContext;

        @Inject
        CatalogAdapter(Context context) {
            mContext = context;
        }

        public void setList(List<Chapter> list) {
            mList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (mList == null)
                return 0;
            return mList.size();
        }

        @Override
        public Chapter getItem(int position) {
            if (mList.size() > position)
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
            Chapter chapter = getItem(position);

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_catalog, parent, false);

                holder.title = convertView.findViewById(R.id.title);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.title.setText(chapter.getTitle());

            return convertView;
        }

        class ViewHolder {
            TextView title;
        }
    }
}
