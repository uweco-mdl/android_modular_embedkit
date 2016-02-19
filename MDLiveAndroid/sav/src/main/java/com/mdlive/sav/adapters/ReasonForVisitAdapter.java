package com.mdlive.sav.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mdlive.sav.MDLiveReasonForVisit;
import com.mdlive.sav.R;

import java.util.ArrayList;

/**
 * This Adapter class is mainly for setting up the symptom  and also can search for the
 * new symptom.The selected symptonm is retrieved through the arraylist and
 * the search function for the new symptom has been done here.
 */
public class ReasonForVisitAdapter extends BaseAdapter implements Filterable{
    private ArrayList<String> originalArray = new ArrayList<String>();
    private ArrayList<String> array = new ArrayList<String>();
    private Activity context;
    private Filter filter;
    private LayoutInflater inflate;
    private Boolean notFound = false;
    private int checkedItemPosition = -1;
    private String checkedItemReaston = "";
    private ImageView btnContinue;
    public ReasonForVisitAdapter(Activity activityContext,
                                 ArrayList<String> arraylist, ImageView btnContinue) {
        this.context = activityContext;
        this.originalArray = arraylist;
        this.array = arraylist;
        this.btnContinue = btnContinue;
        filter= new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // Create a FilterResults object
                FilterResults results = new FilterResults();
               /*  If the constraint (search string/pattern) is null
                 or its length is 0, i.e., its empty then
                 we just set the `values` property to the
                 original contacts list which contains all of them*/
                if (constraint == null || constraint.length() == 0) {
                    results.values = originalArray;
                    results.count = originalArray.size();
                } else {
                  /*  Some search constraint has been passed so let's filter accordingly.*/
                    ArrayList<String> filteredContacts = new ArrayList<String>();

                  /* We'll go through all the contacts and see
                     if they contain the supplied string.*/
                    for (String c : originalArray) {
                        if (c.toUpperCase().contains( constraint.toString().toUpperCase() )) {
                            // if `contains` == true then add it
                            // to our filtered list
                            filteredContacts.add(c);
                        }
                    }

                    // Finally set the filtered values and size/count
                    results.values = filteredContacts;
                    results.count = filteredContacts.size();
                }
                // Return our FilterResults object
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                array = (ArrayList<String>) results.values;
                if(array.size()==0)
                {
                    notFound = true;
                    array.add(0, constraint.toString());
                }else
                {
                    notFound = false;
                }

                notifyDataSetChanged();
            }
        };
    }

    public int getSelectedPosition(){
        return checkedItemPosition;
    }

    public boolean isNotFoundinList(){
        return notFound;
    }
    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int arg0) {
        return array.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }


    @Override
    public Filter getFilter() {
        return filter;
    }

    /**
     *     The getView Method displays the symptom list items.
     *     The datas are fetched from the Arraylist based on the position the data will be placed
     *     in the listview.
     */
    private static class ViewHolder {
        ImageView reasonCheckbox;
        TextView reasonTxt;
        LinearLayout reasonListItem;
    }
    @Override
    public View getView(final int pos, View convertview, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertview == null) {
            inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertview = inflate.inflate(R.layout.mdlive_reasonforvisitbaseadapter, parent,
                    false);
            viewHolder = new ViewHolder();
            viewHolder.reasonCheckbox = (ImageView) convertview.findViewById(R.id.reasonCheckbox);
            viewHolder.reasonTxt = (TextView) convertview.findViewById(R.id.reasonTxt);
            viewHolder.reasonListItem = (LinearLayout) convertview.findViewById(R.id.reasonListItem);
            convertview.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertview.getTag();
        }


        if(notFound){
            viewHolder.reasonCheckbox.setVisibility(View.GONE);
            viewHolder.reasonTxt.setText("No results found for '"+array.get(pos)+"'.\n"+"Submit '"+array.get(pos)+"' as your symptom");
            viewHolder.reasonTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MDLiveReasonForVisit) context).rightBtnOnClick(null);
                }
            });
        }else {
            viewHolder.reasonCheckbox.setVisibility(View.VISIBLE);
            viewHolder.reasonTxt.setText(array.get(pos));

        }
        viewHolder.reasonListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedItemPosition = pos;
                checkedItemReaston = array.get(pos);
                notifyDataSetChanged();
            }
        });

        if(checkedItemPosition >= 0 && (checkedItemPosition == pos || checkedItemReaston.equals(array.get(pos)))){
            viewHolder.reasonCheckbox.setImageResource(R.drawable.check_box_tick);
            btnContinue.setVisibility(View.VISIBLE);
        }else{
            viewHolder.reasonCheckbox.setImageResource(R.drawable.check_box_untick);
        }

        return convertview;
    }
}