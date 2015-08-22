package com.mdlive.embedkit.uilayer.login.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Appointment;

import java.util.List;

/**
 * Created by dhiman_da on 8/17/2015.
 */
public class UpcominAppointmentAdapter extends BaseAdapter {
    private List<Appointment> mAppointments;

    public UpcominAppointmentAdapter(final List<Appointment> appointments) {
        mAppointments = appointments;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mAppointments == null ? 0 : mAppointments.size();
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
        return mAppointments == null ? null : mAppointments.get(position);
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

    public Appointment getAppointment(int position) {
        return mAppointments == null ? null : mAppointments.get(position);
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (viewHolder == null) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.adapter_pending_appoinments, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.adapter_upcoming_appoinment_text_view);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Appointment appointment = mAppointments.get(position);

        final StringBuilder builder = new StringBuilder();
        builder.append(appointment.getPhysicianName() + "\n");
        builder.append(MdliveUtils.convertMiliSeconedsToStringWithTimeZone(appointment.getInMilliseconds(), appointment.getTimeZone()) + "\n");
        builder.append(appointment.getApptType());

        viewHolder.mTextView.setText(builder.toString());

        final int type = MdliveUtils.getRemainigTimeToAppointment(appointment.getInMilliseconds(), appointment.getTimeZone());

        switch (type) {
            case 0 :
                viewHolder.mTextView.setTextColor(convertView.getContext().getResources().getColor(R.color.selected_bg));
                viewHolder.mTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.notification_icon4_upcoming_white, R.color.transparent, R.color.transparent, R.color.transparent);
                break;

            case 1 :
            case 2 :
                viewHolder.mTextView.setTextColor(convertView.getContext().getResources().getColor(R.color.selected_bg));
                viewHolder.mTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.notification_icon4_upcoming, R.color.transparent, R.color.transparent, R.color.transparent);
                break;
        }

        return convertView;
    }

    static class ViewHolder {
        TextView mTextView;
    }
}
