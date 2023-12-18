package org.notifier.testExtractor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetTestsJson {

    private static final String TEST_LINK = "https://ge.apache.org/scan-data/gradle/grvvdpfp6ognw/tests";

    public static String getTestJson() throws IOException {
        String rawJson = HTTPRequest.getRawJsonFromURL(TEST_LINK);
        return extractTestsArray(rawJson);
    }

    private static String extractTestsArray(String rawJsonString) {
        JSONObject jsonObject = new JSONObject(rawJsonString);
        return jsonObject.getJSONObject("data").getJSONArray("tests").toString();
    }

}
