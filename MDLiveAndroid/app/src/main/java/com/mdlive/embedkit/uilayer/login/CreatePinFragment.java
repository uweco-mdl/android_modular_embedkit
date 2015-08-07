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

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;

/**
 * Created by venkataraman_r on 7/23/2015.
 */

public class CreatePinFragment extends MDLiveBaseFragment implements TextWatcher, View.OnClickListener {
    private OnCreatePinCompleted mOnCreatePinCompleted;

    private EditText mPassCode1 = null;
    private EditText mPassCode2 = null;
    private EditText mPassCode3 = null;
    private EditText mPassCode4 = null;
    private EditText mPassCode5 = null;
    private EditText mPassCode6 = null;
    private EditText mPassCode7 = null;
    private TextView mTitle = null;

    private View dummyEditText1 = null;
    private View dummyEditText2 = null;
    private View dummyEditText3 = null;
    private View dummyEditText4 = null;
    private View dummyEditText5 = null;
    private View dummyEditText6 = null;

    public static CreatePinFragment newInstance() {
        final CreatePinFragment createPinFragment = new CreatePinFragment();
        return createPinFragment;
    }

    public CreatePinFragment(){ super(); }

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

        mPassCode1 = (EditText) changePin.findViewById(R.id.passCode1);
        mPassCode2 = (EditText) changePin.findViewById(R.id.passCode2);
        mPassCode3 = (EditText) changePin.findViewById(R.id.passCode3);
        mPassCode4 = (EditText) changePin.findViewById(R.id.passCode4);
        mPassCode5 = (EditText) changePin.findViewById(R.id.passCode5);
        mPassCode6 = (EditText) changePin.findViewById(R.id.passCode6);
        mPassCode7 = (EditText) changePin.findViewById(R.id.dumy_field_passcode);

        dummyEditText1 = (View) changePin.findViewById(R.id.dumy_passcode_field_1);
        dummyEditText2 = (View) changePin.findViewById(R.id.dumy_passcode_field_2);
        dummyEditText3 = (View) changePin.findViewById(R.id.dumy_passcode_field_3);
        dummyEditText4 = (View) changePin.findViewById(R.id.dumy_passcode_field_4);
        dummyEditText5 = (View) changePin.findViewById(R.id.dumy_passcode_field_5);
        dummyEditText6 = (View) changePin.findViewById(R.id.dumy_passcode_field_6);
        mTitle = (TextView) changePin.findViewById(R.id.title);


        mPassCode7.addTextChangedListener(this);
        setFocus(mPassCode1, dummyEditText1);
        mPassCode7.requestFocus();

        dummyEditText1.setOnClickListener(this);
        dummyEditText2.setOnClickListener(this);
        dummyEditText3.setOnClickListener(this);
        dummyEditText4.setOnClickListener(this);
        dummyEditText5.setOnClickListener(this);
        dummyEditText6.setOnClickListener(this);

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() <= 6) {
            if (count != 0) {
                String text = s.charAt(s.length() - 1) + "";
                switch (s.length()) {

                    case 1:
                        mPassCode1.setText(text);
                        setFocus(mPassCode2, dummyEditText2);
                        break;
                    case 2:
                        mPassCode2.setText(text);
                        setFocus(mPassCode3, dummyEditText3);
                        break;
                    case 3:
                        mPassCode3.setText(text);
                        setFocus(mPassCode4, dummyEditText4);
                        break;
                    case 4:
                        mPassCode4.setText(text);
                        setFocus(mPassCode5, dummyEditText5);
                        break;
                    case 5:
                        mPassCode5.setText(text);
                        setFocus(mPassCode6, dummyEditText6);
                        break;
                    case 6:
                        mPassCode6.setText(text);

                        if (mOnCreatePinCompleted != null) {
                            mOnCreatePinCompleted.onCreatePinCompleted(mPassCode7.getText().toString());
                        }
                        break;
                }
            } else {
                switch (s.length() + 1) {
                    case 1:
                        mPassCode1.setText("");
                        setFocus(mPassCode1, dummyEditText1);
                        break;
                    case 2:
                        mPassCode2.setText("");
                        setFocus(mPassCode2, dummyEditText2);
                        break;
                    case 3:
                        mPassCode3.setText("");
                        setFocus(mPassCode3, dummyEditText3);
                        break;
                    case 4:
                        mPassCode4.setText("");
                        setFocus(mPassCode4, dummyEditText4);
                        break;
                    case 5:
                        mPassCode5.setText("");
                        setFocus(mPassCode5, dummyEditText5);
                        break;
                    case 6:
                        mPassCode6.setText("");
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public void setFocus(EditText editText, View dummyEditText) {

        dummyEditText1.setClickable(false);
        mPassCode1.setBackgroundResource(R.drawable.edittext_lostfocus);
        dummyEditText2.setClickable(false);
        mPassCode2.setBackgroundResource(R.drawable.edittext_lostfocus);
        dummyEditText3.setClickable(false);
        mPassCode3.setBackgroundResource(R.drawable.edittext_lostfocus);
        dummyEditText4.setClickable(false);
        mPassCode4.setBackgroundResource(R.drawable.edittext_lostfocus);
        dummyEditText5.setClickable(false);
        mPassCode5.setBackgroundResource(R.drawable.edittext_lostfocus);
        dummyEditText6.setClickable(false);
        mPassCode6.setBackgroundResource(R.drawable.edittext_lostfocus);

        if (editText != null) {
            editText.setBackgroundResource(R.drawable.edittext_focus);
            dummyEditText.setClickable(true);
        }
    }

    @Override
    public void onClick(View v) {
        v.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mPassCode7, InputMethodManager.SHOW_IMPLICIT);
    }

    public interface  OnCreatePinCompleted {
        void onCreatePinCompleted(final String pin);
    }
}