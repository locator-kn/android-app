package com.locator_app.locator.view.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.R;
import com.locator_app.locator.util.BitmapHelper;
import com.locator_app.locator.util.CacheImageLoader;
import com.locator_app.locator.view.bubble.BubbleView;

import java.util.Arrays;
import java.util.List;

public class RowLayoutAdapter extends BaseAdapter {

    public class ViewHolder {
        public TextView text;
        public TextView description;
        public BubbleView bubbleView;
    }

    public class RowLayoutItem {
        public String text;
        public String description;
        public String imageUri;
        public String fallbackImageUri;
    }

    List<RowLayoutItem> rowLayoutItems;
    List<Bitmap> thumbnails;

    public void setRowLayoutItems(List<RowLayoutItem> rowLayoutItems) {
        this.thumbnails = Arrays.asList(new Bitmap[rowLayoutItems.size()]);
        this.rowLayoutItems = rowLayoutItems;
        notifyDataSetChanged();
    }

    public void clear() {
        rowLayoutItems.clear();
        thumbnails.clear();
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
            convertView.setTag(viewHolder);
        }

        final RowLayoutItem item = rowLayoutItems.get(position);
        viewHolder = (ViewHolder)convertView.getTag();

        if (thumbnails.get(position) == null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.bubbleView.getLayoutParams();
            int width = params.width;
            int height = params.height;
            int size = Math.min(width, height);
            loadImage(item.imageUri, item.fallbackImageUri, position, size);
        }

        viewHolder.text.setText(item.text);
        viewHolder.description.setText(item.description);
        viewHolder.bubbleView.setIcon(thumbnails.get(position));

        return convertView;
    }

    private void loadImage(final String imageUri, final String fallbackImageUri,
                           final int position, final int size) {

        // load fallback synchronous
        CacheImageLoader.getInstance().loadSync(fallbackImageUri)
                .subscribe(
                        (fallback) -> {
                            Bitmap rounded = BitmapHelper.getRoundBitmap(fallback, size);
                            this.thumbnails.set(position, rounded);
                            notifyDataSetChanged();
                        },
                        (error) -> { }
                );

        // load real image asynchronous
        CacheImageLoader.getInstance().loadAsync(imageUri)
                .subscribe(
                        (thumbnail) -> {
                            Bitmap rounded = BitmapHelper.getRoundBitmap(thumbnail, size);
                            this.thumbnails.set(position, rounded);
                            notifyDataSetChanged();
                        },
                        (error) -> { }
                );
    }
}
