package com.mdlive.embedkit.uilayer.pharmacy.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdlive.embedkit.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by srinivasan_ka on 5/6/2015.
 */
public class PharmacyListAdaper extends BaseAdapter{

    private ArrayList<HashMap<String, Object>>  list = new ArrayList<HashMap<String, Object>>();
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
        if((Boolean)getItem(position).get("is_preferred")){
            ((TextView) convertView.findViewById(R.id.preferStoretxt)).setVisibility(View.VISIBLE);
        }else{
            ((TextView) convertView.findViewById(R.id.preferStoretxt)).setVisibility(View.GONE);
        }

        if((Boolean)getItem(position).get("twenty_four_hours")){
            ((TextView) convertView.findViewById(R.id.twentyfourHrstxt)).setVisibility(View.VISIBLE);
        }else{
            ((TextView) convertView.findViewById(R.id.twentyfourHrstxt)).setVisibility(View.GONE);
        }

        ((TextView) convertView.findViewById(R.id.pharmacyAddresline1)).setText((String)getItem(position).get("address1"));

        ((TextView) convertView.findViewById(R.id.pharmacyAddressline2)).setText(
                ((getItem(position).get("city")!= null) ? (String)getItem(position).get("city")+", ":"")+
                    (String)getItem(position).get("state")+" "+(String)getItem(position).get("zipcode"));

        ((TextView) convertView.findViewById(R.id.text_view_a)).setText((String)getItem(position).get("store_name"));
        ((TextView) convertView.findViewById(R.id.text_view_b)).setText((((getItem(position).get("distance")!= null) &&
                ((String)getItem(position).get("distance")).length() != 0) ? (String)((String) getItem(position).get("distance")).trim().replace(" miles", "mi"):""));

        setMaxWidthForLeftText(convertView.findViewById(R.id.relative_layout),
                (TextView) convertView.findViewById(R.id.text_view_a),
                (TextView) convertView.findViewById(R.id.text_view_b)
        );
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

    public void setMaxWidthForLeftText(final View parentView, final TextView leftTextView, final TextView rightTextView) {
        parentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                int rWidth, aWidth, bWidth;
                rWidth = parentView.getWidth();
                leftTextView.measure(0, 0);
                aWidth = leftTextView.getMeasuredWidth();
                rightTextView.measure(0, 0);
                bWidth = rightTextView.getMeasuredWidth();
                int aMarginEnd = 0, bMarginStart = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    aMarginEnd = (int) (((RelativeLayout.LayoutParams)leftTextView.getLayoutParams()).getMarginEnd() * context.getResources().getDisplayMetrics().density);
                    bMarginStart = (int) (((RelativeLayout.LayoutParams)rightTextView.getLayoutParams()).getMarginStart() * context.getResources().getDisplayMetrics().density);
                } else {
                    aMarginEnd = 10;
                    bMarginStart = 10;
                }
                leftTextView.setMaxWidth(rWidth - (bWidth + aMarginEnd + bMarginStart));
                leftTextView.invalidate();
                rightTextView.invalidate();
                parentView.invalidate();
            }
        }, 50);
    }
}
