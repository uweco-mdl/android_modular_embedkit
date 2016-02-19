package com.mdlive.embedkit.uilayer.behaviouralhealth;

import org.json.JSONObject;

/**
 * Created by sanjibkumar_p on 7/24/2015.
 */
public class DummyJSON {
    // To be removed later
    public static JSONObject getBehaviouralHistoryResponse() {
        try {
            final String string = "{\"family_hospitalized\":\"Yes\",hospitalized_duration:\"Sfgggwdfvf\",behavioral_health_reasons:[{\"condition\":\"EmotionalProblem\",active:\"No\"},{\"condition\":\"Substance Abuse\",active:\"Yes\"},{\"condition\":\"Relationship Issue\",active:\"Yes\"},{\"condition\":\"Other\",active:\"No\"}],behavioral_family_history:[{\"condition\":\" AlcoholDependence\",active:\"Yes\"},{\"condition\":\"ObsessiveCompulsiveDisorder (OCD)\",active:\"Yes\"},{\"condition\":\"Bipolar Disorder\",active:\"No\"},{\"condition\":\"Schizophrenia\",active:\"No\"},{\"condition\":\"Depression\",active:\"No\"},{\"condition\":\"Substance Abuse\",active:\"No\"}],counseling_preference:\"Female\",hospitalized:\"Yes\",hospitalized_date:\"20 15-01-20\",behavioral_health_description:\"Anger management\",behavioral_mconditions:[{\"condition\":\"Alcohol Dependence\",active:\"No\"},{\"condition\":\"ObsessiveCompulsiveDisorder (OCD)\",active:\"No\"},{\"condition\":\"Anxiety\",active:\"No\"},{\"condition\":\"Panic Attacks\",active:\"No\"},{\"condition\":\"Bipolar Disorder\",active:\"No\"},{\"condition\":\"Schizophrenia\",active:\"No\"},{\"condition\":\"Depression\",active:\"No\"},{\"condition\":\"SubstanceAbuse\",active:\"No\"}]}";
            final JSONObject jsonObject = new JSONObject(string);

            return jsonObject;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getBehaviouralHistoryResponseString() {
        return "{\"family_hospitalized\":\"Yes\",hospitalized_duration:\"Sfgggwdfvf\",behavioral_health_reasons:[{\"condition\":\"EmotionalProblem\",active:\"No\"},{\"condition\":\"Substance Abuse\",active:\"Yes\"},{\"condition\":\"Relationship Issue\",active:\"Yes\"},{\"condition\":\"Other\",active:\"No\"}],behavioral_family_history:[{\"condition\":\" AlcoholDependence\",active:\"Yes\"},{\"condition\":\"ObsessiveCompulsiveDisorder (OCD)\",active:\"Yes\"},{\"condition\":\"Bipolar Disorder\",active:\"No\"},{\"condition\":\"Schizophrenia\",active:\"No\"},{\"condition\":\"Depression\",active:\"No\"},{\"condition\":\"Substance Abuse\",active:\"No\"}],counseling_preference:\"Female\",hospitalized:\"Yes\",hospitalized_date:\"20 15-01-20\",behavioral_health_description:\"Anger management\",behavioral_mconditions:[{\"condition\":\"Alcohol Dependence\",active:\"No\"},{\"condition\":\"ObsessiveCompulsiveDisorder (OCD)\",active:\"No\"},{\"condition\":\"Anxiety\",active:\"No\"},{\"condition\":\"Panic Attacks\",active:\"No\"},{\"condition\":\"Bipolar Disorder\",active:\"No\"},{\"condition\":\"Schizophrenia\",active:\"No\"},{\"condition\":\"Depression\",active:\"No\"},{\"condition\":\"SubstanceAbuse\",active:\"No\"}]}";
    }
}
