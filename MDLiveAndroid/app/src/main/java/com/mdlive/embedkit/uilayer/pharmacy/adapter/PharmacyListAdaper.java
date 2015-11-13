package com.mdlive.embedkit.uilayer.pharmacy.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mdlive.embedkit.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This adapter is used to display pharmacy search results contents.
 *
 * Every list item is inflating layout mdlive_pharm_custom_pharmacy_searchlist_view.
 *
 * Custom layout initialization will be take part at getView() method
 *
 */
public class PharmacyListAdaper extends BaseAdapter{

    private ArrayList<HashMap<String, Object>>  list = new ArrayList<HashMap<String, Object>>();
    private Activity context;

    /**
     * Constructor to create PharmacyListAdaper Instance.
     * @param context - Activity context which will be passed from activity class.
     * @param list - list of searched items to be displayed in listview.
     */
    public PharmacyListAdaper(Activity context, ArrayList<HashMap<String, Object>> list){
        this.context = context;
        this.list = list;
    }

    /**
     * This override method is used to return initialized and updated view to listview.
     *
     * @param convertView - view is going to be return
     * @param position - position of view in listview
     * @param parent - inflated layout group
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.mdlive_pharm_custom_pharmacy_searchlist_view, null);
            convertView.setTag(convertView);
        }else{
            convertView = (View) convertView.getTag();
        }
        if((Boolean)getItem(position).get("is_preferred")){
            convertView.findViewById(R.id.preferStoretxt).setVisibility(View.VISIBLE);
        }else{
            convertView.findViewById(R.id.preferStoretxt).setVisibility(View.GONE);
        }

        if((Boolean)getItem(position).get("twenty_four_hours")){
            convertView.findViewById(R.id.twentyfourHrstxt).setVisibility(View.VISIBLE);
        }else{
            convertView.findViewById(R.id.twentyfourHrstxt).setVisibility(View.GONE);
        }

        ((TextView) convertView.findViewById(R.id.pharmacyAddresline1)).setText((String)getItem(position).get("address1"));

        ((TextView) convertView.findViewById(R.id.pharmacyAddressline2)).setText(
                ((getItem(position).get("city")!= null) ? getItem(position).get("city") +", ":"")+
                        getItem(position).get("state") +" "+ getItem(position).get("zipcode"));

        ((TextView) convertView.findViewById(R.id.text_view_a)).setText((String)getItem(position).get("store_name"));

        ((TextView) convertView.findViewById(R.id.milesText)).setText((((getItem(position).get("distance") != null) &&
                ((String) getItem(position).get("distance")).length() != 0) ? ((String) getItem(position).get("distance")).trim().replace(" miles", "mi") : ""));

        if(convertView.findViewById(R.id.twentyfourHrstxt).getVisibility() == View.VISIBLE){
            setMaxWidthForLeftText(convertView.findViewById(R.id.relative_layout),
                    (TextView) convertView.findViewById(R.id.text_view_a),
                    (LinearLayout) convertView.findViewById(R.id.preferStoretxt)
            );
        }
        return convertView;
    }


    /**
     * returns size of items in list
     */
    @Override
    public int getCount() {
        return list.size();
    }

    /**
     * returns object according to position which is occpy.
     * @param position - position of item in list
     */
    @Override
    public HashMap<String, Object> getItem(int position) {
        return list.get(position);
    }

    /**
     * returns id of Item
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     *  This method is used to shrink pharmacy store name if exceeds screen display
     *
     *  While pharmacy name is shrinking, then there will not be any changes on distance text.
     */
    public void setMaxWidthForLeftText(final View parentView, final TextView leftTextView,
                                       final LinearLayout rightTextView) {
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
                    aMarginEnd = (int) (((LinearLayout.LayoutParams)leftTextView.getLayoutParams()).getMarginEnd() * context.getResources().getDisplayMetrics().density);
                    bMarginStart = (int) (((LinearLayout.LayoutParams)rightTextView.getLayoutParams()).getMarginStart() * context.getResources().getDisplayMetrics().density);
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
