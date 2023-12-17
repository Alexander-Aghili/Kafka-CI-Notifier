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
        String rawJson = getRawJsonFromHTTPRequest(TEST_LINK);
        return extractTestsArray(rawJson);
    }

    private static String getRawJsonFromHTTPRequest(String url) throws IOException {
        URL apiUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                return response.toString();
            }
        } else {
            throw new IOException("HTTP request failed with response code: " + responseCode);
        }
    }

    private static String extractTestsArray(String rawJsonString) {
        JSONObject jsonObject = new JSONObject(rawJsonString);
        return jsonObject.getJSONObject("data").getJSONArray("tests").toString();
    }

}
