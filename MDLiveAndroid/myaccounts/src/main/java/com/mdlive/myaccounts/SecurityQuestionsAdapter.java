package com.mdlive.myaccounts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mdlive.myaccounts.R;

import java.util.ArrayList;

/**
 * Created by venkataraman_r on 8/20/2015.
 */

public class SecurityQuestionsAdapter extends BaseAdapter {

    Context context;
    private static LayoutInflater inflater=null;
    ArrayList<String> values ;

    public SecurityQuestionsAdapter(Context context, ArrayList<String> values ) {
        this.context = context;
        this.values = values;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    public String getSecurityQuestion(final int position) {
        return values == null ? "" : values.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    public class Holder
    {
        TextView tv;

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.security_questions_list, null);
        holder.tv=(TextView) rowView.findViewById(R.id.txtMemberName);

        holder.tv.setText(values.get(position));

        return rowView;
    }

}