package com.mdlive.mobile;

import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.mdlive.utils.RobolectricGradleTestRunner;

import junit.framework.Assert;

import org.json.JSONObject;
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
 * Robolectric Test file for MDLivePharmacyDetails module
 */

@RunWith(RobolectricGradleTestRunner.class)  /* It refers Roboletric Config file*/
public class MDLivePharmacyDetailsTest {

    @Test /* This test scenario is used to check webservice response for a valid PharmacyId input */
    public void validPharmacyIdTest() throws  Exception{

        // Valid Input for Pharmacy Id
        String httpResponse = makeHttpsCallForLoginCredentials(46506L);

        Assert.assertTrue(getUniqueIdFromResponse(httpResponse));

    }

    @Test /* This test scenario is used to check webservice response for a invalid PharmacyId input */
    public void InValidPharmacyIdTest() throws  Exception{

        // InValid Input for Pharmacy Id
        String httpResponse = makeHttpsCallForLoginCredentials(4651111106L);
        Assert.assertFalse(getUniqueIdFromResponse(httpResponse));

    }

    @Test /* This test scenario is used to check webservice response for a zero value input*/
    public void ZeroPharmacyIdTest() throws  Exception{

        // Zero Input for Pharmacy Id
        String httpResponse = makeHttpsCallForLoginCredentials(0L);
        Assert.assertFalse(getUniqueIdFromResponse(httpResponse));

    }

    @Test /* This test scenario is used to check webservice response for a Invalid Number PharmacyId input*/
    public void InValidNumberPharmacyIdTest() throws  Exception{

        // InValid Number Format Input for Pharmacy Id
        String httpResponse = makeHttpsCallForLoginCredentials(00012121212454L);
        Assert.assertFalse(getUniqueIdFromResponse(httpResponse));

    }

    /* This function is basically testing the http response and check for null of field value pharmacyId*/
    public boolean getUniqueIdFromResponse(String httpResponse){
        try{
            if(httpResponse != null && httpResponse.length() != 0){
                JSONObject rootObj = new JSONObject(httpResponse);
                if(rootObj.has("message") && !TextUtils.isEmpty(rootObj.getString("message")) &&
                        rootObj.getString("message").equals("Pharmacy details updated"))
                    return true;
                else
                    return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /* This function handles the https url connection with params username and password and return response from server*/
    public String makeHttpsCallForLoginCredentials(Long pharmacyId)throws Exception{

        //Url link for Use pharmacy details
        URL url = new URL("https://stage-members.mdlive.com/services/pharmacies/update");

        HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json");

        String creds = String.format("%s:%s", "c9e63d9a77f17039c470","b302e84f866a8730eb2");
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT).trim();

        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);

        urlConnection.setRequestMethod("PUT");

        urlConnection.setRequestProperty("Authorization", auth);

        urlConnection.setRequestProperty("RemoteUserId", "49cc043b-55a1-4747-8fbe-613767e1cfe2");

        HashMap<String, Long> gsonMap = new HashMap<String, Long>();
        gsonMap.put("pharmacy_id", pharmacyId);

        try {
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
            return "";
        }

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





