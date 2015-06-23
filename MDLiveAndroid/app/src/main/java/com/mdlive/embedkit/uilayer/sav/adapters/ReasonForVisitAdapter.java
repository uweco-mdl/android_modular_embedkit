package com.mdlive.embedkit.uilayer.sav.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;

import java.util.ArrayList;

/**
 * Created by sudha_s on 5/18/2015.
 */
public class ReasonForVisitAdapter extends BaseAdapter implements Filterable{
   private ArrayList<String> array = new ArrayList<String>();
    private Context context;
    private TextView noResults,submitResults;
    private LinearLayout LinearResults;
    private Filter filter;
    private LayoutInflater inflate;
    private LinearLayout reasonLinLay;

    public ReasonForVisitAdapter(Context applicationContext,
                                 final ArrayList<String> arraylist, final LinearLayout LinearResults, final TextView noresults, final TextView submitResults) {
        this.context = applicationContext;
        this.array = arraylist;
        this.noResults=  noresults;
        this.LinearResults= LinearResults;
        this.submitResults = submitResults;
        filter= new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // Create a FilterResults object
                FilterResults results = new FilterResults();

                // If the constraint (search string/pattern) is null
                // or its length is 0, i.e., its empty then
                // we just set the `values` property to the
                // original contacts list which contains all of them
                if (constraint == null || constraint.length() == 0) {
                    results.values = arraylist;
                    results.count = arraylist.size();
                }
                else {
                    // Some search copnstraint has been passed
                    // so let's filter accordingly
                    ArrayList<String> filteredContacts = new ArrayList<String>();

                    // We'll go through all the contacts and see
                    // if they contain the supplied string
                    for (String c : arraylist) {
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
                    LinearResults.setVisibility(View.VISIBLE);
                    if(!constraint.toString().trim().isEmpty()) {
                        noresults.setVisibility(View.VISIBLE);
                        submitResults.setVisibility(View.VISIBLE);
                        noresults.setText(context.getResources().getString(R.string.no_results_found_txt));
                        submitResults.setText(java.text.MessageFormat.format(context.getResources().getString(R.string.no_results_found_as_symptom_txt) ,new String[]{constraint.toString()}));
                    } else {
                        noresults.setVisibility(View.GONE);
                        submitResults.setVisibility(View.GONE);
                    }

                }else
                {
                    LinearResults.setVisibility(View.GONE);
                }

                notifyDataSetChanged();
            }

        };


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
     *     The getView Method displays the provider list items.
     *     The datas are fetched from the Arraylist based on the position the dats will be placed
     *     in the listview.
     */

    @Override
    public View getView(int pos, View convertview, ViewGroup parent) {
        TextView ReasonForVisitTxt;
        ImageView callImg;

        View row;

            inflate = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflate.inflate(R.layout.mdlive_reasonforvisitbaseadapter, parent,
                    false);
        ReasonForVisitTxt = (TextView) row.findViewById(R.id.reasonTxt);
        ReasonForVisitTxt.setText(array.get(pos));


            /**
             *    This is to Check the availability of the Doctor.if the next availabilty of doctor
             *    is available then the time stamp should be  visible else it should be hidden.
             *
             *
             */



        return row;
    }

    /**
     *      @param selectedSymptom : Pass the selected symptom String
     *      The Corresponding symptom will be saved and added to the list and will be
     *      triggerred in the Requird places.
     *
     *
     */


    public void SaveZipCodeCity(String selectedSymptom)
    {
        SharedPreferences settings = context.getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PreferenceConstants.NORESULTS_PREFERENCES,selectedSymptom);
        editor.commit();
    }



}