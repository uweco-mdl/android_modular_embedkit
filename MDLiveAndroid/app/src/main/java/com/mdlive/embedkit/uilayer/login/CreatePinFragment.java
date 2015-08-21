package com.mdlive.embedkit.uilayer.login;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

/**
 * Created by venkataraman_r on 7/23/2015.
 */

public class CreatePinFragment extends MDLiveBaseFragment implements TextWatcher, View.OnClickListener {
    private OnCreatePinCompleted mOnCreatePinCompleted;

    private ToggleButton mPassCode1 = null;
    private ToggleButton mPassCode2 = null;
    private ToggleButton mPassCode3 = null;
    private ToggleButton mPassCode4 = null;
    private ToggleButton mPassCode5 = null;
    private ToggleButton mPassCode6 = null;

    private EditText mPassCode7 = null;

    private TextView mTitleTextView;

    public CreatePinFragment() {
        super();
    }

    public static CreatePinFragment newInstance() {
        final CreatePinFragment createPinFragment = new CreatePinFragment();
        return createPinFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mOnCreatePinCompleted = (OnCreatePinCompleted) activity;
        } catch (ClassCastException cce) {
            logE("Error", cce.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragments_change_pin, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnCreatePinCompleted = null;
    }

    public void init(View changePin) {

        mPassCode1 = (ToggleButton) changePin.findViewById(R.id.passCode1);
        mPassCode2 = (ToggleButton) changePin.findViewById(R.id.passCode2);
        mPassCode3 = (ToggleButton) changePin.findViewById(R.id.passCode3);
        mPassCode4 = (ToggleButton) changePin.findViewById(R.id.passCode4);
        mPassCode5 = (ToggleButton) changePin.findViewById(R.id.passCode5);
        mPassCode6 = (ToggleButton) changePin.findViewById(R.id.passCode6);

        mPassCode7 = (EditText) changePin.findViewById(R.id.etPasscode);

        mPassCode7.addTextChangedListener(this);
        mPassCode7.requestFocus();

        mTitleTextView = (TextView) changePin.findViewById(R.id.fragment_change_pin_text_view);
        mTitleTextView.setText(R.string.please_create_a_6_digit_pin);

        changePin.findViewById(R.id.dont_use_pin_text_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCreatePinCompleted != null) {
                    mOnCreatePinCompleted.onClickNoPin();
                }
            }
        });
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int iLength = mPassCode7.getText().length();
        switch (iLength) {
            case 0:
                mPassCode1.setChecked(false);
                mPassCode2.setChecked(false);
                mPassCode3.setChecked(false);
                mPassCode4.setChecked(false);
                mPassCode5.setChecked(false);
                mPassCode6.setChecked(false);
                break;
            case 1:
                mPassCode1.setChecked(true);
                mPassCode2.setChecked(false);
                mPassCode3.setChecked(false);
                mPassCode4.setChecked(false);
                mPassCode5.setChecked(false);
                mPassCode6.setChecked(false);
                break;
            case 2:
                mPassCode1.setChecked(true);
                mPassCode2.setChecked(true);
                mPassCode3.setChecked(false);
                mPassCode4.setChecked(false);
                mPassCode5.setChecked(false);
                mPassCode6.setChecked(false);
                break;
            case 3:
                mPassCode1.setChecked(true);
                mPassCode2.setChecked(true);
                mPassCode3.setChecked(true);
                mPassCode4.setChecked(false);
                mPassCode5.setChecked(false);
                mPassCode6.setChecked(false);
                break;
            case 4:
                mPassCode1.setChecked(true);
                mPassCode2.setChecked(true);
                mPassCode3.setChecked(true);
                mPassCode4.setChecked(true);
                mPassCode5.setChecked(false);
                mPassCode6.setChecked(false);
                break;
            case 5:
                mPassCode1.setChecked(true);
                mPassCode2.setChecked(true);
                mPassCode3.setChecked(true);
                mPassCode4.setChecked(true);
                mPassCode5.setChecked(true);
                mPassCode6.setChecked(false);
                break;
            case 6:
                mPassCode1.setChecked(true);
                mPassCode2.setChecked(true);
                mPassCode3.setChecked(true);
                mPassCode4.setChecked(true);
                mPassCode5.setChecked(true);
                mPassCode6.setChecked(true);
                break;
        }
        if (iLength == 6) {
            MdliveUtils.hideKeyboard(getActivity(), (View) mPassCode7);
            if (mOnCreatePinCompleted != null) {
                mOnCreatePinCompleted.onCreatePinCompleted(mPassCode7.getText().toString());
            }
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View v) {
        v.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mPassCode7, InputMethodManager.SHOW_IMPLICIT);
    }

    public String getEnteredPin() {
        return mPassCode7 == null ? null : mPassCode7.toString().trim();
    }

    public interface OnCreatePinCompleted {
        void onCreatePinCompleted(final String pin);

        void onClickNoPin();
    }
}