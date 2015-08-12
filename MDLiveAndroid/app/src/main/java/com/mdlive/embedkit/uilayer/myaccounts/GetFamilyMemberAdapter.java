package com.mdlive.embedkit.uilayer.myaccounts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;

//import com.squareup.picasso.Picasso;

/**
 * Created by venkataraman_r on 7/27/2015.
 */
public class GetFamilyMemberAdapter extends BaseAdapter {

    Context context;
    private static LayoutInflater inflater=null;
    HashMap<String,ArrayList<String>> values ;

    public GetFamilyMemberAdapter(Context context, HashMap<String,ArrayList<String>> values ) {
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

    @Override
    public long getItemId(int position) {

        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.myaccounts_family_list, null);
        holder.tv=(TextView) rowView.findViewById(R.id.memberUsername);
        holder.img=(RoundedImageView) rowView.findViewById(R.id.memberImage);

        ArrayList<String>name = values.get("NAME");
        ArrayList<String>url = values.get("URL");

        holder.tv.setText(name.get(position));
        //Picasso.with(context).load(url.get(position)).placeholder(R.drawable.profilepic).error(R.drawable.profilepic).into(holder.img);

        return rowView;
    }

}