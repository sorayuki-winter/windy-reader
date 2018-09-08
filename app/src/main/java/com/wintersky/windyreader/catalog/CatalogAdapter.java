package com.wintersky.windyreader.catalog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wintersky.windyreader.R;
import com.wintersky.windyreader.data.Chapter;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmBaseAdapter;
import io.realm.RealmList;

public class CatalogAdapter extends RealmBaseAdapter<Chapter> {

    private int mIndex;

    @Inject
    CatalogAdapter() {
        super(null);
    }

    public void updateData(int index, RealmList<Chapter> list) {
        mIndex = index;
        updateData(list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_catalog, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.setChapter(getItem(position));
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.title) TextView title;
        @BindView(R.id.mark) FrameLayout mark;
        @BindColor(R.color.catalogRead) int colorRead;
        @BindColor(R.color.catalogUnread) int colorUnread;

        ViewHolder(View view) {ButterKnife.bind(this, view);}

        void setChapter(Chapter chapter) {
            if (chapter != null) {
                title.setText(chapter.getTitle().trim());
                title.setTextColor(chapter.isRead() ? colorRead : colorUnread);
                mark.setVisibility(chapter.index == mIndex ? View.VISIBLE : View.INVISIBLE);
            } else {
                title.setText(null);
                mark.setVisibility(View.INVISIBLE);
            }
        }
    }
}
