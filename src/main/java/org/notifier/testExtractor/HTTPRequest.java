package org.notifier.testExtractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Future;

import org.asynchttpclient.*;

import static org.asynchttpclient.Dsl.*;

public class HTTPRequest {
    public static String getRawJsonFromURL(String url) throws IOException {
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

    public static ListenableFuture<Response> asyncHttpRequest(String url) {
        AsyncHttpClient asyncHttpClient=asyncHttpClient();

        return asyncHttpClient.prepareGet(url).execute();
    }
}
