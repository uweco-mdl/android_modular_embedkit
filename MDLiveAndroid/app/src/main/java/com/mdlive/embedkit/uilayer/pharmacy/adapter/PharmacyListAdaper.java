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
 * This adapter class is used to set search results of pharmacies
 * By clicking on the view will redirect to MDLivePharmacyDetails Page.
 *
 * This adapter has constructor with params context and list
 *
 */
public class PharmacyListAdaper extends BaseAdapter {
    private ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    private Activity context;

    public PharmacyListAdaper(Activity context, ArrayList<HashMap<String, Object>> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.mdlive_pharm_custom_pharmacy_searchlist_view, null);
            convertView.setTag(convertView);
        } else {
            convertView = (View) convertView.getTag();
        }
        ((TextView) convertView.findViewById(R.id.pharmacyName)).setText((String) getItem(position).get("store_name"));
        ((TextView) convertView.findViewById(R.id.pharmacyAddresline1)).setText((String) getItem(position).get("address1"));
        ((TextView) convertView.findViewById(R.id.pharmacyAddressline2)).setText(
                (String) getItem(position).get("state") + " " + (String) getItem(position).get("zipcode"));
        ((TextView) convertView.findViewById(R.id.milesText)).setText(
                (String) getItem(position).get("distance"));
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
