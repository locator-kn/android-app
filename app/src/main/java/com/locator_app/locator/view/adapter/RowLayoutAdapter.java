package com.locator_app.locator.view.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.R;
import com.locator_app.locator.view.bubble.BubbleView;

import java.util.LinkedList;
import java.util.List;

public class RowLayoutAdapter extends BaseAdapter {

    public class ViewHolder {
        public TextView text;
        public TextView description;
        public TextView bubbleInfo;
        public BubbleView bubbleView;
    }

    public static class RowLayoutItem {
        public String text;
        public String description;
        public String imageUri;
        public String creationDate;
    }

    List<RowLayoutItem> rowLayoutItems = new LinkedList<>();

    public void setRowLayoutItems(List<RowLayoutItem> rowLayoutItems) {
        this.rowLayoutItems = rowLayoutItems;
        notifyDataSetChanged();
    }

    public void clear() {
        rowLayoutItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return rowLayoutItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowLayoutItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) LocatorApplication.getAppContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rowlayout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView)convertView.findViewById(R.id.text);
            viewHolder.description = (TextView)convertView.findViewById(R.id.description);
            viewHolder.bubbleView = (BubbleView)convertView.findViewById(R.id.bubbleView);
            viewHolder.bubbleInfo = (TextView)convertView.findViewById(R.id.bubbleInfoText);
            convertView.setTag(viewHolder);
        }

        final RowLayoutItem item = rowLayoutItems.get(position);
        viewHolder = (ViewHolder)convertView.getTag();

        viewHolder.text.setText(item.text);
        viewHolder.description.setText(item.description);
        viewHolder.bubbleView.loadImage(item.imageUri);
        viewHolder.bubbleInfo.setText(item.creationDate);

        return convertView;
    }
}
