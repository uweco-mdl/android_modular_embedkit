package com.mdlive.embedkit.uilayer.messagecenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.CircularNetworkImageView;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.SentMessage;

/**
 * Created by dhiman_da on 6/27/2015.
 */
public class MessageSentAdapter extends ArrayAdapter<SentMessage> {
    public MessageSentAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.adapter_message_sent, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.mCircularNetworkImageView = (CircularNetworkImageView) convertView.findViewById(R.id.adapter_message_sent_image_view);
            viewHolder.mTextViewTop = (TextView) convertView.findViewById(R.id.adapter_message_sent_top_text_view);
            viewHolder.mTextViewBottom = (TextView) convertView.findViewById(R.id.adapter_message_sent_bottom_text_view);
            viewHolder.mTextViewTime = (TextView) convertView.findViewById(R.id.adapter_message_sent_date_text_view);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mCircularNetworkImageView.setImageUrl(getItem(position).providerImageUrl, ApplicationController.getInstance().getImageLoader(parent.getContext()));
        viewHolder.mTextViewTop.setText(getItem(position).from);
        viewHolder.mTextViewBottom.setText(getItem(position).subject);

        viewHolder.mTextViewTime.setText(getItem(position).date);
        if (getItem(position).readStatus) {
            viewHolder.mTextViewTime.setCompoundDrawables(null, null, null, parent.getContext().getResources().getDrawable(R.drawable.icon_checklist));
        } else {
            viewHolder.mTextViewTime.setCompoundDrawables(null, null, null, null);
        }

        return convertView;
    }

    private static class ViewHolder {
        CircularNetworkImageView mCircularNetworkImageView;
        TextView mTextViewTop;
        TextView mTextViewBottom;
        TextView mTextViewTime;
    }
}
