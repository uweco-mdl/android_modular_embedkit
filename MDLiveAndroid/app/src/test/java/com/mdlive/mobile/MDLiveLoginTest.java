package com.mdlive.mobile;


import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.gson.Gson;
import com.mdlive.mobile.uilayer.MDLiveLogin;
import com.mdlive.unifiedmiddleware.parentclasses.bean.request.Login;
import com.mdlive.unifiedmiddleware.parentclasses.bean.request.LoginBean;
import com.mdlive.utils.RobolectricGradleTestRunner;
import org.fest.assertions.api.Assertions;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static org.fest.assertions.api.ANDROID.assertThat;

/**
 * Robolectric Test file for Login module
 */

@RunWith(RobolectricGradleTestRunner.class)  /* It refers Roboletric Config file*/
public class MDLiveLoginTest {

    // MDLiveLogin Instance
    private MDLiveLogin mActivity;

    /*This setup is called initially when Test Process Initiated. Here activity instance will be initialzed. */
    @Before
    public void setup() {
        mActivity = Robolectric.buildActivity(MDLiveLogin.class).create().get();
    }

     /*This is a sequence of Test function will be run on sequentially */

    @Test /*This Test Scenario is to check whether Activity Instance is available or not.*/
    public void check_MDLBTLoginActivity_Instance_Test() throws Exception {
        org.junit.Assert.assertTrue(Robolectric.buildActivity(MDLiveLogin.class).create().get() != null);
    }

    @Test /*This Test Scenario is to check whether Activity Instance is available or not.*/
    public void check_MDLBTLoginActivity_Views_Properties_Test() throws Exception {

        // Another way to check text of View.
        Button loginButton = (Button) mActivity.findViewById(R.id.LoginBtn);

        //check view is initialized or not
        assertThat(loginButton).isNotNull();

        assertThat(loginButton).containsText("LOGIN");
    }

    @Test /*This test scenario is used to test login page field values is initially empty or not .*/
    public void loginPage_EmptyCredentials_Test()throws Exception {

        // Another way to check text of View.
        Button loginButton = (Button) mActivity.findViewById(R.id.LoginBtn);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                assertThat((((EditText) mActivity.findViewById(R.id.UserNameEt)))).containsText("");

                assertThat((((EditText) mActivity.findViewById(R.id.PasswordEt)))).containsText("");

            }
        });

        loginButton.performClick();
    }

    @Test /* This test scenario is used to check response from server if empty user credentials passed over webservice*/
    public void emptyUserCredentailsTestForLogin() throws  Exception{

        String httpResponse = makeHttpsCallForLoginCredentials("", ""); /*Username, password*/

        Assertions.assertThat(getUniqueIdFromResponse(httpResponse)).isNull();

    }

    @Test/* This test scenario is used to check response from server if valid user credentials passed over webservice*/
    public void validUserCredentailsTestForLogin() throws  Exception {

        String httpResponse = makeHttpsCallForLoginCredentials("stagefeb5", "mdlive789"); /*Username, password*/

        Assertions.assertThat(getUniqueIdFromResponse(httpResponse)).isNotEqualTo(null);

    }

    @Test/* This test scenario is used to check response from server if invalid user credentials passed over webservice*/
    public void invalidUserCredentailsTestForLogin() throws  Exception{

        String httpResponse = makeHttpsCallForLoginCredentials("mytestapp", "mdliveapp"); /*Username, password*/

        Assertions.assertThat(getUniqueIdFromResponse(httpResponse)).isNull();

    }

    @Test/* This test scenario is used to check response from server if valid user-password with empty username value passed over webservice*/
    public void userNameEmptyUserCredentailsTestForLogin() throws  Exception{

        String httpResponse = makeHttpsCallForLoginCredentials("", "mdlive789"); /*Username, password*/

        Assertions.assertThat(getUniqueIdFromResponse(httpResponse)).isNull();

    }

    @Test/* This test scenario is used to check response from server if valid user-name with empty password value passed over webservice*/
    public void passwordEmptyUserCredentailsTestForLogin() throws  Exception{

        String httpResponse = makeHttpsCallForLoginCredentials("stagefeb5", "");

        Assertions.assertThat(getUniqueIdFromResponse(httpResponse)).isNull();

    }

    /* This function is basically testing the http response and check for null of field value uniqueid*/
    public String getUniqueIdFromResponse(String httpResponse){
        try{
            JSONObject rootObj = new JSONObject(httpResponse);
            if(rootObj.getString("uniqueid").equals("null") || rootObj.getString("uniqueid").equals(null))
                return null;
            else
                return rootObj.getString("uniqueid");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /* This function handles the https url connection with params username and password and return response from server*/
    public String makeHttpsCallForLoginCredentials(String username, String password)throws Exception{

        //Url link for call login action
        URL url = new URL("https://stage-members.mdlive.com/services/customer_logins");

        HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json");

        String creds = String.format("%s:%s", "c9e63d9a77f17039c470","b302e84f866a8730eb2");
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT).trim();

        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);

        urlConnection.setRequestProperty("Authorization", auth);
        urlConnection.setRequestProperty("RemoteUserId", "MobileUser");

        LoginBean loginBean = new LoginBean();
        loginBean.setLogin(new Login(username, password));

        try {
            Gson gson = new Gson();
            String params = gson.toJson(loginBean);
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write(params);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        InputStream in = urlConnection.getInputStream();

        return convertInputStreamToString(in);
    }

    /* This function is used to convert Inputstream Datas to String type*/
    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }


}





