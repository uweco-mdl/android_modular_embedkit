package com.mdlive.embedkit.uilayer.pharmacy.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.mdlive.embedkit.R;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by srinivasan_ka on 5/6/2015.
 */
public class PharmacyListAdaper extends BaseAdapter{

    private ArrayList<HashMap<String, Object>>  list = new ArrayList<HashMap<String, Object>>();
    private int walgreensPosition = -1;
    private Activity context;

    public PharmacyListAdaper(Activity context, ArrayList<HashMap<String, Object>> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.mdlive_pharm_custom_pharmacy_searchlist_view, null);
            convertView.setTag(convertView);
        }else{
            convertView = (View) convertView.getTag();
        }
        if(walgreensPosition < 0 && ((String)getItem(position).get("store_name")).contains("Walgreen")){
            walgreensPosition = position;
        }

        if(walgreensPosition >= 0 && position == walgreensPosition){
            ((TextView) convertView.findViewById(R.id.preferStoretxt)).setVisibility(View.VISIBLE);
        }else{
            ((TextView) convertView.findViewById(R.id.preferStoretxt)).setVisibility(View.GONE);
        }

        if((Boolean)getItem(position).get("twenty_four_hours")){
            ((TextView) convertView.findViewById(R.id.twentyfourHrstxt)).setVisibility(View.VISIBLE);
        }

        ((TextView) convertView.findViewById(R.id.pharmacyName)).setText(
                (String)getItem(position).get("store_name")+" "+
                        (((getItem(position).get("distance")!= null) &&
                        ((String)getItem(position).get("distance")).length() != 0) ? (String)getItem(position).get("distance")+"mi":""));

        ((TextView) convertView.findViewById(R.id.pharmacyAddresline1)).setText((String)getItem(position).get("address1"));

        ((TextView) convertView.findViewById(R.id.pharmacyAddressline2)).setText(
                ((getItem(position).get("city")!= null) ? (String)getItem(position).get("city")+", ":"")+
                    (String)getItem(position).get("state")+" "+(String)getItem(position).get("zipcode"));

        return convertView;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public HashMap<String, Object> getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
