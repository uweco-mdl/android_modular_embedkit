package com.mdlive.embedkit.uilayer.messagecenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Record;

/**
 * Created by dhiman_da on 6/28/2015.
 */
public class RecordAdapter extends ArrayAdapter<Record> {
    public RecordAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.adapter_record, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.adapter_record_image_view);
            viewHolder.mTextViewTop = (TextView) convertView.findViewById(R.id.adapter_record_top_text_view);
            viewHolder.mTextViewBottom = (TextView) convertView.findViewById(R.id.adapter_record_bottom_text_view);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //viewHolder.mImageView.setImageBitmap(whatever);
        viewHolder.mTextViewTop.setText(getItem(position).docName);
        viewHolder.mTextViewBottom.setText(getItem(position).docType + " - " +getItem(position).uploadedAt + " by " + getItem(position).uploadedBy);

        return convertView;
    }

    private static class ViewHolder {
        ImageView mImageView;
        TextView mTextViewTop;
        TextView mTextViewBottom;
    }
}
