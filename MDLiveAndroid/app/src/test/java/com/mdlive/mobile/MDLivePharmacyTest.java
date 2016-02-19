package com.mdlive.mobile;

import android.util.Base64;

import com.mdlive.utils.RobolectricGradleTestRunner;

import org.fest.assertions.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Robolectric Test file for MDLBTPharmacy module
 */
@RunWith(RobolectricGradleTestRunner.class)  /* It refers Roboletric Config file*/
public class MDLivePharmacyTest {


    @Test /* This test scenario is used to check response from server if valid authorization inputs for header*/
    public void validAuthorizationInputsTest() throws  Exception {

        String creds = String.format("%s:%s", "c9e63d9a77f17039c470","b302e84f866a8730eb2");
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT).trim();

        // Required Response from server for Valid Header Details
        Assertions.assertThat(makeHttpsCallForUserInformation(auth)).isNotNull();
    }

    @Test/* This test scenario is used to check response from server if invalid authorization inputs for header*/
    public void invalidAuthorizationInputsTest() throws  Exception {

        String creds = String.format("%s:%s", "c9e63d9a77f17039c470","adsfasdf");
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT).trim();

        // Required Response from server for Invalid Header Details
        Assertions.assertThat(makeHttpsCallForUserInformation(auth)).isNull();
    }

    @Test/* This test scenario is used to check response from server if empty authorization inputs for header*/
    public void emptyAuthorizationInputsTest() throws  Exception {

        String creds = String.format("%s:%s", "","");
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT).trim();

        // Required Response from server for Empty Header Details
        Assertions.assertThat(makeHttpsCallForUserInformation(auth)).isNull();
    }


    public String makeHttpsCallForUserInformation(String auth) throws Exception {
        URL url = new URL("https://stage-rtl.mdlive.com/services/pharmacies/current/");
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoOutput(true);

        urlConnection.setRequestProperty("Authorization", auth);
        urlConnection.setRequestProperty("RemoteUserId", "49cc043b-55a1-4747-8fbe-613767e1cfe2");

        //Condition to check the response code if it is success it will return the result else return null;
        if (urlConnection.getResponseCode() == 200) {
            InputStream in = urlConnection.getInputStream();
            return convertInputStreamToString(in);
        } else {
            return null;
        }

    }

    /* This function is used to convert stream of datas to String type*/
    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }

}
