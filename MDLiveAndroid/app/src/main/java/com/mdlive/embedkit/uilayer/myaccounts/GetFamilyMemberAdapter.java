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
    ArrayList<String>name =new ArrayList<>();
    ArrayList<String>url =new ArrayList<>();
    public GetFamilyMemberAdapter(Context context, ArrayList<String> nameList,ArrayList<String> urlList ) {
        this.context = context;
        name = nameList;
        url = urlList;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return name.size();
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
        Holder holder;
        View rowView = convertView;

        if(convertView == null) {

            holder=new Holder();

            rowView = inflater.inflate(R.layout.myaccounts_family_list, null);
            holder.tv = (TextView) rowView.findViewById(R.id.txtMemberName);
            holder.type = (TextView) rowView.findViewById(R.id.txtMemberType);
            holder.img = (CircularNetworkImageView) rowView.findViewById(R.id.imgMemberPic);

            rowView.setTag(holder);
        }
        else{
            holder=(Holder)rowView.getTag();}

            holder.tv.setText(name.get(position));
            holder.img.setImageUrl(url.get(position), ApplicationController.getInstance().getImageLoader(context));

        return rowView;
    }

}