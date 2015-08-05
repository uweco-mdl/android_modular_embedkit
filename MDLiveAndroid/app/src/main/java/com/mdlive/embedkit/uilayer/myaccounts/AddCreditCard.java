package com.mdlive.embedkit.uilayer.myaccounts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mdlive.embedkit.R;

/**
 * Created by venkataraman_r on 7/26/2015.
 */
public class AddCreditCard extends Fragment {

    Toolbar toolbar;
    private TextView toolbarTitle;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View addCreditCard = inflater.inflate(R.layout.fragments_add_creditcard,null);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getResources().getString(R.string.billing_title));

        TextView addCard = (TextView)addCreditCard.findViewById(R.id.btn_addCreditCard);

        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.tabcontent, CreditCardInfoFragment.newInstance(null)).commit();
            }
        });

        return addCreditCard;

    }
}
