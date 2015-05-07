package com.mdlive.mobile.uilayer.sav;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.mdlive.unifiedmiddleware.parentclasses.activity.sav.UMWGetStarted;
import com.mdlive.unifiedmiddleware.commonclasses.utils.Utils;
import com.mdlive.mobile.R;

/**
 * Created by unnikrishnan_b on 4/6/2015.
 */
public class MDLiveGetStarted extends UMWGetStarted {
    Button Yes,No;
    ImageView select_edit;
    String[] names={"Please Choose","Gone to the Emergency Room","Used Urgent Care","Seen my provider in Person","Done nothing"};

    AlertDialog levelDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Utils.hideSoftKeyboard(this);
//        setData(null,(Button)findViewById(R.id.SavContinueBtn)); // TODO : Replace the null when the next screen is ready
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_started);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        select_edit = (ImageView)findViewById(R.id.selectlist);

        select_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(select_edit.getWindowToken(), 0);
                //TODO : Remove hardcoded text
                final CharSequence[] items = {"Gone to the Emergency Room "," Seen my provider in    Person "," Done nothing "};

                // Creating and Building the Dialog

                AlertDialog.Builder builder = new AlertDialog.Builder(MDLiveGetStarted.this);
                builder.setTitle("Please Choose");

                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {


                        switch(item)
                        {
                            case 0:
                                // Your code when first option seletced
                                break;
                            case 1:
                                // Your code when 2nd  option seletced

                                break;
                            case 2:
                                // Your code when 3rd option seletced
                                break;
                            case 3:
                                // Your code when 4th  option seletced
                                break;

                        }
                        levelDialog.dismiss();
                    }
                });

                levelDialog = builder.create();
                levelDialog.show();
            }
        });


        Yes = (Button) findViewById(R.id.Button_yes);
        No = (Button) findViewById(R.id.Button_no);
        Yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Yes.setBackgroundResource(R.drawable.btn_shapes);
                No.setBackgroundResource(R.drawable.shapes);
                No.setTextColor(Color.parseColor("#A4A4A4"));
            }
        });
        No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                No.setBackgroundResource(R.drawable.btn_shapes);
                Yes.setBackgroundResource(R.drawable.shapes);
                Yes.setTextColor(Color.parseColor("#A4A4A4"));
            }
        });

    }

}

