package com.mdlive.embedkit.uilayer.lifestyle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mdlive.embedkit.R;

import java.util.List;

/**
 * Created by sanjibkumar_p on 7/20/2015.
 */
public class LifeStyleBaseAdapter extends BaseAdapter {
    private List<Model> mModels;

    public LifeStyleBaseAdapter(Context context, List<Model> objects) {
        mModels = objects;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mModels == null ? 0 : mModels.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return mModels == null ? null : mModels.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<Model> getItems() {
        return mModels;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.mdlive_lifestyle_addrows, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.life_style_question_text);
            viewHolder.mRadioGroup = (RadioGroup) convertView.findViewById(R.id.rootradiogroup);
            viewHolder.mYesRadioButton = (RadioButton) convertView.findViewById(R.id.yesradioButton);
            viewHolder.mNoRadioButton = (RadioButton) convertView.findViewById(R.id.noradioButton);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Model model = mModels.get(position);
        viewHolder.mTextView.setText(model.condition);
        viewHolder.mRadioGroup.setOnCheckedChangeListener(null);

        if (Model.YES.equalsIgnoreCase(mModels.get(position).active)) {
            viewHolder.mRadioGroup.setTag(Model.YES);
            viewHolder.mYesRadioButton.setChecked(true);
        } else if(Model.NO.equalsIgnoreCase(mModels.get(position).active)) {
            viewHolder.mRadioGroup.setTag(Model.NO);
            viewHolder.mNoRadioButton.setChecked(true);
        } else {
            viewHolder.mRadioGroup.setTag(null);
            viewHolder.mRadioGroup.clearCheck();
        }
        viewHolder.mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(viewHolder.mRadioGroup.getTag() != null){
                    if (checkedId == R.id.yesradioButton) {
                        mModels.get(position).active = Model.YES;
                    } else if (checkedId == R.id.noradioButton) {
                        mModels.get(position).active = Model.NO;
                    }
                }else{
                    if (checkedId == R.id.yesradioButton) {
                        mModels.get(position).active = Model.YES;
                    } else if (checkedId == R.id.noradioButton) {
                        mModels.get(position).active = Model.NO;
                    }
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView mTextView;
        RadioGroup mRadioGroup;
        RadioButton mYesRadioButton;
        RadioButton mNoRadioButton;
    }
}
