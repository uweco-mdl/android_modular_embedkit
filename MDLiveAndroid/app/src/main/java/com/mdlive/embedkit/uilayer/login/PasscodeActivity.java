package com.mdlive.embedkit.uilayer.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.commonclasses.constants.PreferenceConstants;

public class PasscodeActivity extends Activity {

	EditText dumyEditText, editText1, editText2, editText3, editText4, editText5, editText6;
	Bundle extras;
	String callbackID, passcodeDataPage;
	TextView titleText, descText, forgotButton, secDescText;
	FrameLayout headerLayout;
    View coverEditText1, coverEditText2, coverEditText3, coverEditText4, coverEditText5, coverEditText6;
	Boolean isBackEnable;
	JsonParser jsonParse;
	JsonObject passcodeDataJson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mdlive_passcode_screen);
        editText1 = (EditText)findViewById(R.id.passcode_field_1);
        dumyEditText =  (EditText)findViewById(R.id.dumy_field_passcode);
//		extras = getIntent().getExtras();
//		callbackID = extras.getString("callback_id");
//		passcodeDataPage = extras.getString("passcode_data_page");
		titleText = (TextView)findViewById(R.id.title_text);
		descText = (TextView)findViewById(R.id.desc_text);
		secDescText = (TextView)findViewById(R.id.second_desc_text);
		forgotButton = (TextView)findViewById(R.id.forgot_pin);
		headerLayout = (FrameLayout)findViewById(R.id.header_layout);
//		forgotButton.setText(MDLiveLocalizationSystem.localizedStringForKey("Forgot pin"));
		forgotButton.setText(("Forgot pin"));
		jsonParse = new JsonParser();
//		passcodeDataJson = jsonParse.parse(passcodeDataPage).getAsJsonObject();
//        titleText.setText(passcodeDataJson.get("title").getAsString());
//		descText.setText(passcodeDataJson.get("desc").getAsString());
        TextView signIn = (TextView) findViewById(R.id.loginWithUserName);
        TextView signUpText = (TextView) findViewById(R.id.SignUpText);
        SharedPreferences settings = getSharedPreferences(PreferenceConstants.MDLIVE_USER_PREFERENCES, 0);
//        final String imageURI = settings.getString("AFFILIATION_LOGO_URL",null);
//        final ImageView imageView = (ImageView) findViewById(R.id.passcode_logo);
//        imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        final Handler handler = new Handler(){
//            @Override
//            public void handleMessage(android.os.Message msg) {
//                Drawable drawable = new PictureDrawable(svg.renderToPicture());
//                imageView.setImageDrawable(drawable);
//                onClickPinBox(dumyEditText);
//            }
//        };
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    URL url = new URL(imageURI);
//                    svg = SVG.getFromInputStream((InputStream) url.getContent());
//                    handler.sendEmptyMessage(0);
//                } catch(Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }).start();

//		if(passcodeDataJson.get("isShowForgotButton").getAsBoolean()){
			forgotButton.setVisibility(View.VISIBLE);
            signIn.setVisibility(View.VISIBLE);
//            signIn.setText(MDLiveLocalizationSystem.localizedStringForKey("Login with Username"));
            signIn.setText(("Login with Username"));
            signIn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent data = new Intent();
                    data.putExtra("passcode_pin", "user_sign_in");
                    setResult(RESULT_OK, data);
                    finish();
                }
            });
            signUpText.setVisibility(View.VISIBLE);
            signUpText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent data = new Intent();
                    data.putExtra("passcode_pin", "user_sign_up");
                    setResult(RESULT_OK, data);
                    finish();
                }
            });
//		}else{
//			forgotButton.setVisibility(View.INVISIBLE);
//            signIn.setVisibility(View.INVISIBLE);
//            signUpText.setVisibility(View.INVISIBLE);
//		}

//		if(passcodeDataJson.get("isShowHeader").getAsBoolean()){
//			headerLayout.setVisibility(View.VISIBLE);
//		}else{
//            onClickPinBox(dumyEditText);
//			headerLayout.setVisibility(View.GONE);
//		}

//		if(passcodeDataJson.has("second_desc") && !passcodeDataJson.get("second_desc").isJsonNull() && !passcodeDataJson.get("second_desc").getAsString().isEmpty()) {
//			secDescText.setText(passcodeDataJson.get("second_desc").getAsString());
//			secDescText.setVisibility(View.VISIBLE);
//		} else {
//			secDescText.setVisibility(View.GONE);
//		}
//        if(passcodeDataJson.has("password_unlock")){
//            TextView password_unlock = (TextView) findViewById(R.id.password_unlock);
//            password_unlock.setText(passcodeDataJson.get("password_unlock").getAsString());
////            ((TextView) findViewById(R.id.password_unlock_Def)).setText(MDLiveLocalizationSystem.localizedStringForKey("password_unlock_Def"));
//            ((TextView) findViewById(R.id.password_unlock_Def)).setText(("password_unlock_Def"));
//            password_unlock.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View arg0) {
//
//                    Intent data = new Intent();
//                    data.putExtra("passcode_pin", "password_unlock");
//                    setResult(RESULT_OK, data);
//                    finish();
//                }
//            });
//        }
//		isBackEnable = passcodeDataJson.get("isUsingBackButton").getAsBoolean();

		forgotButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				Intent data = new Intent();
				data.putExtra("passcode_pin", "forgot_pin");
				setResult(RESULT_OK, data);
				finish();
			}
		});

//        editText1.setContentDescription(MDLiveLocalizationSystem.localizedStringForKey("nat_pc_first"));
		editText2 = (EditText)findViewById(R.id.passcode_field_2);
//        editText2.setContentDescription(MDLiveLocalizationSystem.localizedStringForKey("nat_pc_second"));
		editText3 = (EditText)findViewById(R.id.passcode_field_3);
//        editText3.setContentDescription(MDLiveLocalizationSystem.localizedStringForKey("nat_pc_third"));
		editText4 = (EditText)findViewById(R.id.passcode_field_4);
//        editText4.setContentDescription(MDLiveLocalizationSystem.localizedStringForKey("nat_pc_fourth"));
		editText5 = (EditText)findViewById(R.id.passcode_field_5);
//        editText5.setContentDescription(MDLiveLocalizationSystem.localizedStringForKey("nat_pc_fifth"));
		editText6 = (EditText)findViewById(R.id.passcode_field_6);
//        editText6.setContentDescription(MDLiveLocalizationSystem.localizedStringForKey("nat_pc_sixth"));

		coverEditText1 = (View)findViewById(R.id.dumy_passcode_field_1);
//        coverEditText1.setContentDescription(MDLiveLocalizationSystem.localizedStringForKey("nat_pc_first_active"));
		coverEditText2 = (View)findViewById(R.id.dumy_passcode_field_2);
//        coverEditText2.setContentDescription(MDLiveLocalizationSystem.localizedStringForKey("nat_pc_second_active"));
		coverEditText3 = (View)findViewById(R.id.dumy_passcode_field_3);
//        coverEditText3.setContentDescription(MDLiveLocalizationSystem.localizedStringForKey("nat_pc_third_active"));
		coverEditText4 = (View)findViewById(R.id.dumy_passcode_field_4);
//        coverEditText4.setContentDescription(MDLiveLocalizationSystem.localizedStringForKey("nat_pc_fourth_active"));
		coverEditText5 = (View)findViewById(R.id.dumy_passcode_field_5);
        //coverEditText5.setContentDescription(MDLiveLocalizationSystem.localizedStringForKey("nat_pc_fifth_active"));
		coverEditText6 = (View)findViewById(R.id.dumy_passcode_field_6);
//        coverEditText6.setContentDescription(MDLiveLocalizationSystem.localizedStringForKey("nat_pc_sixth_active"));

		dumyEditText.requestFocus();
		setPinBoxBackground(editText1, 1);

		dumyEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {


				if(s.length() <= 6){
					if(count != 0 ){
						String text = s.charAt(s.length()-1)+"";

						switch (s.length()) {
						case 1:
							editText1.setText(text);
							setPinBoxBackground(editText2, 2);
							break;
						case 2:
							editText2.setText(text);
							setPinBoxBackground(editText3, 3);
							break;
						case 3:
							editText3.setText(text);
							setPinBoxBackground(editText4, 4);
							break;
						case 4:
							editText4.setText(text);
							setPinBoxBackground(editText5, 5);
							break;
						case 5:
							editText5.setText(text);
							setPinBoxBackground(editText6, 6);
							break;
						case 6:
							editText6.setText(text);
                            InputMethodManager imm = (InputMethodManager)getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(editText6.getWindowToken(), 0);
                            Intent data = new Intent(getApplicationContext(),MDLiveDashboard.class);
							data.putExtra("passcode_pin", dumyEditText.getText().toString());
//							setResult(RESULT_OK, data);
                            startActivity(data);
							finish();
							break;
						default:break;
						}
					}else{
						switch (s.length()+1) {
						case 1:
							editText1.setText("");
							setPinBoxBackground(editText1, 1);
							break;
						case 2:
							editText2.setText("");
							setPinBoxBackground(editText2, 2);
							break;
						case 3:
							editText3.setText("");
							setPinBoxBackground(editText3, 3);
							break;
						case 4:
							editText4.setText("");
							setPinBoxBackground(editText4, 4);
							break;
						case 5:
							editText5.setText("");
							setPinBoxBackground(editText5, 5);
							break;
						case 6:
							editText6.setText("");
							break;
						default:break;
						}
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}

			@Override
			public void afterTextChanged(Editable s) {}
		});

	}

	public void onClickPinBox(View v){
		dumyEditText.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(dumyEditText, InputMethodManager.SHOW_IMPLICIT);
	}

    /**
     *
     * Sets background color for the pin-boxes depending on their box index.
     *
     * @param editText Corresponding EditText.
     * @param boxIndex Index of the pin box
     */
	public void setPinBoxBackground(EditText editText, int boxIndex){

		switch (boxIndex) {
		case 1:
			editText1.setBackgroundColor(Color.parseColor("#BDBDBD"));
			editText2.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText3.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText4.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText5.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText6.setBackgroundColor(Color.parseColor("#F2F2F2"));

			coverEditText1.setClickable(true);
            coverEditText1.requestFocus();
			coverEditText2.setClickable(false);
			coverEditText3.setClickable(false);
			coverEditText4.setClickable(false);
			coverEditText5.setClickable(false);
			coverEditText6.setClickable(false);

			break;
		case 2:
			editText1.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText2.setBackgroundColor(Color.parseColor("#BDBDBD"));
			editText3.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText4.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText5.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText6.setBackgroundColor(Color.parseColor("#F2F2F2"));

			coverEditText1.setClickable(false);
			coverEditText2.setClickable(true);
            coverEditText2.requestFocus();
			coverEditText3.setClickable(false);
			coverEditText4.setClickable(false);
			coverEditText5.setClickable(false);
			coverEditText6.setClickable(false);

			break;
		case 3:
			editText1.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText2.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText3.setBackgroundColor(Color.parseColor("#BDBDBD"));
			editText4.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText5.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText6.setBackgroundColor(Color.parseColor("#F2F2F2"));

			coverEditText1.setClickable(false);
			coverEditText2.setClickable(false);
			coverEditText3.setClickable(true);
            coverEditText3.requestFocus();
			coverEditText4.setClickable(false);
			coverEditText5.setClickable(false);
			coverEditText6.setClickable(false);

			break;
		case 4:
			editText1.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText2.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText3.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText4.setBackgroundColor(Color.parseColor("#BDBDBD"));
			editText5.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText6.setBackgroundColor(Color.parseColor("#F2F2F2"));

			coverEditText1.setClickable(false);
			coverEditText2.setClickable(false);
			coverEditText3.setClickable(false);
			coverEditText4.setClickable(true);
            coverEditText4.requestFocus();
			coverEditText5.setClickable(false);
			coverEditText6.setClickable(false);

			break;
		case 5:
			editText1.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText2.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText3.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText4.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText5.setBackgroundColor(Color.parseColor("#BDBDBD"));
			editText6.setBackgroundColor(Color.parseColor("#F2F2F2"));

			coverEditText1.setClickable(false);
			coverEditText2.setClickable(false);
			coverEditText3.setClickable(false);
			coverEditText4.setClickable(false);
			coverEditText5.setClickable(true);
            coverEditText5.requestFocus();
			coverEditText6.setClickable(false);

			break;
		case 6:
			editText1.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText2.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText3.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText4.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText5.setBackgroundColor(Color.parseColor("#F2F2F2"));
			editText6.setBackgroundColor(Color.parseColor("#BDBDBD"));

			coverEditText1.setClickable(false);
			coverEditText2.setClickable(false);
			coverEditText3.setClickable(false);
			coverEditText4.setClickable(false);
			coverEditText5.setClickable(false);
			coverEditText6.setClickable(true);
            coverEditText6.requestFocus();
			break;
		default:break;
		}
	}

	@Override
	public void onBackPressed() {
		if(isBackEnable){
			Intent data = new Intent();
			data.putExtra("passcode_pin", "passcode_back");
			setResult(RESULT_CANCELED, data);
			finish();
		}
	}

}
