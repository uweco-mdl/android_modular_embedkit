package com.mdlive.embedkit.uilayer.login.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.CircularNetworkImageView;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.User;

import java.util.List;

/**
 * Created by dhiman_da on 8/13/2015.
 */
public class DashBoardSpinnerAdapter extends ArrayAdapter<User> {
    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public DashBoardSpinnerAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.adapter_dashboard_spinner, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.adapter_dashboard_spinner_text_view);
            viewHolder.mImageView = (CircularNetworkImageView) convertView.findViewById(R.id.adapter_dashboard_spinner_circular_image_view);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mTextView.setText(getItem(position).mName);
        if (User.MODE_ADD_CHILD == getItem(position).mMode) {
            viewHolder.mImageView.setImageResource(R.drawable.doctor_icon);
        } else {
            viewHolder.mImageView.setImageUrl(getItem(position).mImageUrl, ApplicationController.getInstance().getImageLoader(convertView.getContext()));
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolderDropDown viewHolder = null;

        if (convertView == null) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.adapter_dash_borad_spinner_dropdown, parent, false);

            viewHolder = new ViewHolderDropDown();
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.adapter_dash_borad_spinner_dropdown_text_view);
            viewHolder.mImageView = (CircularNetworkImageView) convertView.findViewById(R.id.adapter_dash_borad_spinner_dropdown_circular_image_view);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderDropDown) convertView.getTag();
        }

        viewHolder.mTextView.setText(getItem(position).mName);
        if (User.MODE_ADD_CHILD == getItem(position).mMode) {
            viewHolder.mImageView.setImageResource(R.drawable.doctor_icon);
        } else {
            viewHolder.mImageView.setImageUrl(getItem(position).mImageUrl, ApplicationController.getInstance().getImageLoader(convertView.getContext()));
        }

        return convertView;
    }

    static class ViewHolder {
        CircularNetworkImageView mImageView;
        TextView mTextView;
    }

    static class ViewHolderDropDown {
        CircularNetworkImageView mImageView;
        TextView mTextView;
    }
}
