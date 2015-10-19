package com.mdlive.embedkit.uilayer.helpandsupport;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mdlive.embedkit.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by sanjibkumar_p on 7/14/2015.
 */
public class HelpAndSupportAdapter extends ArrayAdapter<HashMap<String,String>> {

    ViewHolder viewHolder;
    public List<HashMap<String, String>> itemList;
    private Activity mContext;
    LayoutInflater inflater;

    public HelpAndSupportAdapter(Activity context, List<HashMap<String, String>> itemList) {
        super(context, R.layout.mdlive_help_and_support_addrows, itemList);
        this.itemList = itemList;
        this.mContext = context;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        if (itemList != null)
            return itemList.size();
        return 0;
    }

    public HashMap<String, String> getItem(int position) {
        if (itemList != null)
            return itemList.get(position);
        return null;
    }

    static class ViewHolder {

        private TextView questionTextView;
        private TextView answerTextView;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
//		ViewHolder viewHolder;
        View v = convertView;
        final HashMap<String, String> mRecords = itemList.get(position);

        if (v == null) {
            v = inflater.inflate(R.layout.mdlive_help_and_support_addrows, parent, false);
			viewHolder = new ViewHolder();

            viewHolder.questionTextView = (TextView) v.findViewById(R.id.helpsupportquestion);
            viewHolder.answerTextView = (TextView) v.findViewById(R.id.helpsupportanswer);

            v.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) v.getTag();

        }

        viewHolder.questionTextView.setText(mRecords.get("question"));
        viewHolder.answerTextView.setText(mRecords.get("answer"));

        return v;
    }

}
