package com.mdlive.embedkit.uilayer.login;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdlive.embedkit.R;

import java.util.ArrayList;

/**
 * Created by venkataraman_r on 7/16/2015.
 */
public class DrawerAdapter extends BaseAdapter {

    /**
     * LayoutInflater instance for inflating the requested layout in the list view
     */
    private LayoutInflater mInflater;

    private ArrayList<String> mDataSet;

    private TypedArray images;

    /**
     * Default constructor
     */
    public DrawerAdapter(Context context, ArrayList<String> dataSet,TypedArray images) {

        mInflater = LayoutInflater.from(context);
        mDataSet = dataSet;
        this.images= images;
    }

    public int getCount() {
        return mDataSet.size();
    }

    public Object getItem(int index) {
        return mDataSet.get(index);
    }

    public long getItemId(int index) {
        return index;
    }

    public View getView(int position, View recycledView, ViewGroup parent) {
        ViewHolder holder;



        if (recycledView == null) {

            holder = new ViewHolder();
            recycledView = mInflater.inflate(R.layout.item_drawer_list, parent, false);
            holder.title = (TextView) recycledView.findViewById(R.id.title);
            holder.image = (ImageView)recycledView.findViewById(R.id.image);

            recycledView.setTag(holder);

        } else {
            holder = (ViewHolder) recycledView.getTag();
        }

        holder.title.setText(mDataSet.get(position));
        holder.image.setImageResource(images.getResourceId(position, -1));
        return recycledView;
    }

    private static class ViewHolder {
        TextView title;
        ImageView image;
    }
}

