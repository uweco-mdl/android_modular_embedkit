package com.mdlive.embedkit.uilayer.mdliveassist;


import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mdlive.embedkit.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MDLiveAssistFragment extends Fragment {

    private View view;

    private ListView mListView;
    ListAdapter adapter;

    public static MDLiveAssistFragment newInstance() {
        final MDLiveAssistFragment fragment = new MDLiveAssistFragment();
        return fragment;
    }

    public MDLiveAssistFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.mdlive_assist_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        showCallDialog();
    }

    private void showCallDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            final View view = layoutInflater.inflate(R.layout.alertdialogmessage, null);
            ImageView alertImage = (ImageView)view.findViewById(R.id.alertdialogimageview);
            alertImage.setImageResource(R.drawable.ic_launcher);
            TextView alertText = (TextView)view.findViewById(R.id.alertdialogtextview);
            alertText.setText(getActivity().getText(R.string.mdl_call_text));

            builder.setView(view);
            builder.setPositiveButton(getActivity().getText(R.string.mdl_call),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {

                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + getActivity().getText(R.string.mdl_callnumber)));
                                getActivity().startActivity(intent);

                            } catch (Exception e) {

                            }
                        }
                    });
            builder.setNegativeButton(getActivity().getText(R.string.mdl_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        dialog.dismiss();

                    } catch (Exception e) {

                    }

                }
            });

            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
