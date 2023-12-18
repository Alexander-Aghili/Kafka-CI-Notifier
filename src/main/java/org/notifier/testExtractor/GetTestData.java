package org.notifier.testExtractor;

import org.json.JSONArray;
import org.json.JSONObject;
import org.notifier.testAnalysis.Datapoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class GetTestData {
    public GetTestData() {}

    public static ArrayList<Datapoint> getTestDataFromLink(String link) throws IOException {
        ArrayList<Datapoint> datapoints = new ArrayList<>();

        JSONArray datapointsJson = getDatapointsJson(link);
        for (Object datapointObj : datapointsJson) {
            JSONObject datapoint = (JSONObject) datapointObj;
            datapoints.add(new Datapoint(datapoint));
        }
        Collections.sort(datapoints);
        return datapoints;
    }

    private static JSONArray getDatapointsJson(String link) throws IOException {
        String rawJson = new HTTPRequest().getRawJsonFromURL(link);
        return extractDatapointsFromRawJson(rawJson);
    }

    private static JSONArray extractDatapointsFromRawJson(String rawJson) {
        JSONObject object = new JSONObject(rawJson);
        return object.getJSONObject("data").getJSONObject("outcomeTrend").getJSONArray("dataPoints");
    }
}
