package com.mdlive.embedkit.uilayer.myaccounts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.customUi.CircularNetworkImageView;

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
        TextView type;
        CircularNetworkImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.myaccounts_family_list, null);
        holder.tv=(TextView) rowView.findViewById(R.id.txtMemberName);
        holder.type=(TextView) rowView.findViewById(R.id.txtMemberType);
        holder.img=(CircularNetworkImageView) rowView.findViewById(R.id.imgMemberPic);

        ArrayList<String>name = values.get("NAME");
        ArrayList<String>url = values.get("URL");

        if(position == 0) {
            holder.tv.setText(name.get(position));
            holder.img.setImageUrl(url.get(position), ApplicationController.getInstance().getImageLoader(context));
            holder.type.setText("Primary");
        }
        else
        {
            holder.tv.setText(name.get(position));
            holder.img.setImageUrl(url.get(position), ApplicationController.getInstance().getImageLoader(context));
        }
        //Picasso.with(context).load(url.get(position)).placeholder(R.drawable.profilepic).error(R.drawable.profilepic).into(holder.img);

        return rowView;
    }

}