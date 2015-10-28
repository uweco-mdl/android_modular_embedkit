package com.mdlive.myhealth.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdlive.myhealth.R;
import com.mdlive.myhealth.activity.ListItem;

import java.util.List;

/**
 * Created by sanjibkumar_p on 7/28/2015.
 */
public class MyHealthListAdapter extends ArrayAdapter<ListItem> {
    public MyHealthListAdapter(Context context, int resource, List<ListItem> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //TextView textView;
        ViewHolder viewHolder;

        if (convertView == null) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.adapter_health_list, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.adapter_health_list_text_view);
            viewHolder.subTitleTextView = (TextView) convertView.findViewById(R.id.adapter_health_list_sub_text_view);
            viewHolder.leftImageView = (ImageView) convertView.findViewById(R.id.adapter_health_list_left_imagview);

            convertView.setTag(viewHolder);
            //convertView.setTag(textView);
        } else {
            //textView = (TextView) convertView.getTag();
            viewHolder = (ViewHolder)convertView.getTag();
        }

        // when Textview is declared inside getview method then the following we have to use
        //This should be called when ViewHolder class is not created
        //textView.setText(getItem(position).mString);
        // when set the drawableleft in textview this is required for specific position
        //textView.setCompoundDrawablesWithIntrinsicBounds(textView.getResources().getDrawable(getItem(position).mDrawableId), null, null, null);

        // This should be called when ViewHolder class is created
        viewHolder.titleTextView.setText(getItem(position).mString);
        viewHolder.subTitleTextView.setText(getItem(position).mSubTitleString);
        viewHolder.leftImageView.setImageResource(getItem(position).mDrawableId);

        return convertView;
    }

    public static class ViewHolder {
        TextView titleTextView;
        TextView subTitleTextView;
        ImageView leftImageView;
    }
}
