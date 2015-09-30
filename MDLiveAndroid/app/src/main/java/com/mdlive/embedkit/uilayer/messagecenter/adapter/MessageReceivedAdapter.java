package com.mdlive.embedkit.uilayer.messagecenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.CircularNetworkImageView;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.ReceivedMessage;

/**
 * Created by dhiman_da on 6/25/2015.
 */
public class MessageReceivedAdapter extends ArrayAdapter<ReceivedMessage> {
    public MessageReceivedAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        //if (convertView == null) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.adapter_message_received, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.mReadImageView = (ImageView) convertView.findViewById(R.id.adapter_message_received_read_image_view);
            viewHolder.mCircularNetworkImageView = (CircularNetworkImageView) convertView.findViewById(R.id.adapter_message_received_image_view);
            viewHolder.mTextViewTop = (TextView) convertView.findViewById(R.id.adapter_message_received_top_text_view);
            viewHolder.mTextViewBottom = (TextView) convertView.findViewById(R.id.adapter_message_received_bottom_text_view);
            viewHolder.mTextViewTime = (TextView) convertView.findViewById(R.id.adapter_message_received_date_text_view);

            //convertView.setTag(viewHolder);
        //} else {
            //viewHolder = (ViewHolder) convertView.getTag();
        //}


        if (getItem(position).readStatus) {
            viewHolder.mTextViewTop.setTextColor(convertView.getResources().getColor(R.color.read_text_color));
            viewHolder.mTextViewBottom.setTextColor(convertView.getResources().getColor(R.color.read_text_color));
            viewHolder.mTextViewTime.setTextColor(convertView.getResources().getColor(R.color.read_text_color));
            viewHolder.mReadImageView.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.mTextViewTop.setTextColor(convertView.getResources().getColor(R.color.unread_text_color));
            viewHolder.mTextViewBottom.setTextColor(convertView.getResources().getColor(R.color.unread_text_color));
            viewHolder.mTextViewTime.setTextColor(convertView.getResources().getColor(R.color.unread_text_color));
            viewHolder.mReadImageView.setVisibility(View.VISIBLE);
        }

        viewHolder.mCircularNetworkImageView.setImageUrl(getItem(position).providerImageUrl, ApplicationController.getInstance().getImageLoader(parent.getContext()));
        viewHolder.mTextViewTop.setText(getItem(position).from);
        viewHolder.mTextViewBottom.setText(getItem(position).subject);
        viewHolder.mTextViewTime.setText(TimeZoneUtils.getReceivedSentTime(getItem(position).inMilliseconds, getItem(position).timeZone, getContext()));

        return convertView;
    }

    private static class ViewHolder {
        CircularNetworkImageView mCircularNetworkImageView;
        TextView mTextViewTop;
        TextView mTextViewBottom;
        TextView mTextViewTime;
        ImageView mReadImageView;
    }
}
