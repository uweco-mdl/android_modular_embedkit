package com.mdlive.messages.messagecenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mdlive.messages.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.CircularNetworkImageView;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.MyProvider;

/**
 * Created by dhiman_da on 6/27/2015.
 */
public class ProviderAdapter extends ArrayAdapter<MyProvider> {
    public ProviderAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.new_adapter_layout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.mCircularNetworkImageView = (CircularNetworkImageView) convertView.findViewById(R.id.adapter_provider_image_view);
            viewHolder.mTextViewTop = (TextView) convertView.findViewById(R.id.adapter_provider_top_text_view);
            viewHolder.mTextViewBottom = (TextView) convertView.findViewById(R.id.adapter_provider_bottom_text_view);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mCircularNetworkImageView.setImageUrl(getItem(position).providerImageUrl, ApplicationController.getInstance().getImageLoader(parent.getContext()));
        viewHolder.mTextViewTop.setText(getItem(position).name);
        //viewHolder.mTextViewBottom.setText(convertView.getResources().getString(R.string.mdli_provider_since) + getItem(position).providerSince);
        if(getItem(position).lastAppointment!=0) {
            viewHolder.mTextViewBottom.setText(convertView.getResources().getString(R.string.mdl_last_visit) + TimeZoneUtils.getReceivedSentTime(getItem(position).lastAppointment, "", getContext()));
        } else {
            viewHolder.mTextViewBottom.setVisibility(View.GONE);
        }
        return convertView;
    }

    private static class ViewHolder {
        CircularNetworkImageView mCircularNetworkImageView;
        TextView mTextViewTop;
        TextView mTextViewBottom;
    }
}
