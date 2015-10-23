package com.mdlive.embedkit.uilayer.myhealth.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.CircularNetworkImageView;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.ConsultationHistory;

import java.lang.reflect.Method;

/**
 * Created by unnikrishnan_b on 8/22/2015.
 */
public class ConsultationHistoryAdapter extends ArrayAdapter<ConsultationHistory> {
    private Context context;
    private int selectedPosition = -1;
    private View selectedView;

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    public ConsultationHistoryAdapter(Context context, int resource, int textViewResourceId) {

        super(context, resource, textViewResourceId);
        this.context = context;
    }

    public View getSelectedView(){
        return selectedView;
    }

    public void setSelectedView(View view){
        selectedView = view;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        //if (convertView == null) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.consultation_history_adapter_layout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.mCircularNetworkImageView = (CircularNetworkImageView) convertView.findViewById(R.id.adapter_provider_image_view);
            viewHolder.mTextViewTop = (TextView) convertView.findViewById(R.id.adapter_provider_top_text_view);
            viewHolder.mTextViewBottom = (TextView) convertView.findViewById(R.id.adapter_provider_bottom_text_view);
            viewHolder.reasonForVisitTv = (TextView) convertView.findViewById(R.id.adapter_reason_for_visit_text_view);
            viewHolder.primaryDiagnosisTv = (TextView) convertView.findViewById(R.id.adapter_primary_diagnosis_text_view);
            viewHolder.sendMessageTv = (TextView) convertView.findViewById(R.id.adapter_send_msg_text_view);
            viewHolder.afterCareInstructionsTv = (TextView) convertView.findViewById(R.id.adapter_aftercare_instructions_text_view);
            viewHolder.claimFormTv = (TextView) convertView.findViewById(R.id.adapter_view_claim_form_text_view);

            //convertView.setTag(viewHolder);
        //} else {
            //viewHolder = (ViewHolder) convertView.getTag();
        //}
        viewHolder.mCircularNetworkImageView.setImageUrl(getItem(position).getProviderImageUrl(), ApplicationController.getInstance().getImageLoader(parent.getContext()));
        viewHolder.mTextViewTop.setText(getItem(position).getProviderName());
        viewHolder.mTextViewBottom.setText(context.getResources().getString(R.string.mdl_last_visit) + " " + getItem(position).getConsultationDate() + " by " + getItem(position).getConsultationMethod());
        viewHolder.reasonForVisitTv.setText(" " + getItem(position).getChiefComplaint());
        if (getItem(position).getPrimaryDiagnosis().isEmpty()) {
            viewHolder.primaryDiagnosisTv.setText(" " + context.getResources().getString(R.string.mdl_no_diagnosis));
        } else {
            String diagnosisText = "";
            for (String item : getItem(position).getPrimaryDiagnosis()) {
                diagnosisText += "," + item;
            }
            viewHolder.primaryDiagnosisTv.setText(" " + diagnosisText.substring(1, diagnosisText.length()));
        }

        viewHolder.sendMessageTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchComposeMessage(getItem(position));
            }
        });

        if (getItem(position).getDischargeFormUrl() == null || getItem(position).getDischargeFormUrl().isEmpty()) {
            viewHolder.afterCareInstructionsTv.setTextColor(getContext().getResources().getColor(R.color.gray_c));
        } else {
            viewHolder.afterCareInstructionsTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchBrowserIntent(getItem(position).getDischargeFormUrl());
                }
            });
        }

        if (getItem(position).getClaimFormUrl() == null || getItem(position).getClaimFormUrl().isEmpty()) {
            viewHolder.claimFormTv.setTextColor(getContext().getResources().getColor(R.color.gray_c));
        } else {
            viewHolder.claimFormTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchBrowserIntent(getItem(position).getClaimFormUrl());
                }
            });
        }
        if (position == selectedPosition) {
            convertView.findViewById(R.id.history_details_ll).setVisibility(View.VISIBLE);
        } else {
            convertView.findViewById(R.id.history_details_ll).setVisibility(View.GONE);
        }
        return convertView;
    }

    private static class ViewHolder {
        CircularNetworkImageView mCircularNetworkImageView;
        TextView mTextViewTop, claimFormTv;
        TextView mTextViewBottom, reasonForVisitTv, primaryDiagnosisTv, sendMessageTv, afterCareInstructionsTv;
    }

    private void launchBrowserIntent(String url) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (browserIntent.resolveActivity(getContext().getPackageManager()) != null) {
                context.startActivity(browserIntent);
            } else {
                MdliveUtils.showDialog(context, context.getString(R.string.mdl_app_name), context.getString(R.string.mdl_no_compitable_app));
            }
        } catch (Exception e) {

        }
    }

    private void launchComposeMessage(ConsultationHistory consultationHistory){
        try {
            Class clazz = Class.forName("com.mdlive.messages.messagecenter.MessageCenterComposeActivity");
            Method method = clazz.getMethod("getMessageComposeDetailsIntentWithHeading", Context.class, Parcelable.class, String.class);
            context.startActivity( (Intent) method.invoke(null, context, consultationHistory, context.getString(R.string.mdl_send_message_caps)));
        } catch (ClassNotFoundException e){
            Toast.makeText(context, context.getString(R.string.mdl_mdlive_messages_module_not_found), Toast.LENGTH_LONG).show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

