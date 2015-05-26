package com.mdlive.mobile;


import android.util.Base64;

import com.google.gson.Gson;
import com.mdlive.utils.RobolectricGradleTestRunner;

import org.fest.assertions.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Robolectric Test file for MDLiveChooseProvider module
 */

@RunWith(RobolectricGradleTestRunner.class)  /* It refers Roboletric Config file*/
public class MDLiveChooseProviderTest {

    @Test /* This test scenario is used to check response from server for valid provider type input*/
    public void validLocationProviderInputTest() throws Exception {

        // Required Response from server for Valid LocatedIn Inputs
        Assertions.assertThat(makeHttpsCallForProviderList("CA", "3")).isNotNull();/* located_in, provider_type*/
    }

    @Test/* This test scenario is used to check response from server for invalid provider type input*/
    public void invalidLocationProviderInputTest() throws Exception {

        // Required Response from server for Invalid LocatedIn Inputs
        Assertions.assertThat(makeHttpsCallForProviderList("CA", "15")).isNull(); /* located_in, provider_type*/

    }

    @Test/* This test scenario is used to check response from server for empty provider type input*/
    public void emptyLocationProviderInputTest() throws Exception {

        // Required Response from server for empty LocatedIn Inputs
        Assertions.assertThat(makeHttpsCallForProviderList("CA", "")).isNull(); /* located_in, provider_type*/

    }


    /* This function handles the https url connection with UserInformation Details and return response from server*/
    public String makeHttpsCallForProviderList(String located_in, String provider_type) throws Exception {

        //Url link for choose provider details
        URL url = new URL("https://stage-rtl.mdlive.com/services/providers/search_providers");

        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json");

        String creds = String.format("%s:%s", "c9e63d9a77f17039c470", "b302e84f866a8730eb2");
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT).trim();

        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);

        urlConnection.setRequestProperty("Authorization", auth);
        urlConnection.setRequestProperty("RemoteUserId", "49cc043b-55a1-4747-8fbe-613767e1cfe2");

        try {
            HashMap<String, String> gsonMap = new HashMap<String, String>();
            gsonMap.put("located_in", located_in);
            gsonMap.put("provider_type", provider_type);
            Gson gson = new Gson();
            String params = gson.toJson(gsonMap);
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write(params);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(urlConnection.getResponseCode() == 200){
            InputStream in = urlConnection.getInputStream();
            return convertInputStreamToString(in);
        }else{
            return null;
        }
    }


    /* This function is used to convert Inputstream Datas to String type*/
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





