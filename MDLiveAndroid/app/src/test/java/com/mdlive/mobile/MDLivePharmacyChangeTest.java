package com.mdlive.mobile;

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
* Robolectric Test file for MDLBTPharmacyChange module
*/
@RunWith(RobolectricGradleTestRunner.class)  /* It refers Roboletric Config */
public class MDLivePharmacyChangeTest {


    @Test
    public void checkSuggestion() throws Exception{
        //Method to check with valid inputs
        Assertions.assertThat(getPharamcies("gh")).isNotEmpty();
    }
    @Test
    public void checkInvalidSuggestion() throws Exception{
        //Method to check with invalid inputs
        Assertions.assertThat(getPharamcies("@%$6$E6")).isNotNull();
    }

    @Test
    public void checkemptySuggestion() throws Exception{
        //Method to check with invalid inputs
        Assertions.assertThat(getPharamcies("")).isNull();
    }


    /* This function handles the https url connection with post  params (ie) user typed text and return the suggestion based on the typed text from server*/
    public String getPharamcies(String typedText) throws Exception{
        URL url = new URL("https://stage-rtl.mdlive.com/services/pharmacies/suggest_pharmacy");
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestProperty("Authorization", "Basic YzllNjNkOWE3N2YxNzAzOWM0NzA6YjMwMmU4NGY4NjZhODczMGViMg==");
        urlConnection.setRequestProperty("RemoteUserId", "49cc043b-55a1-4747-8fbe-613767e1cfe2");
        HashMap<String, String> queryMap = new HashMap<String, String>();
        queryMap.put("query", typedText);
        try {
            Gson gson = new Gson();
            String postBody = gson.toJson(queryMap);
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write(postBody);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
