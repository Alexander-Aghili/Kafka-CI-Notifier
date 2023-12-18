package org.notifier.testExtractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.util.stream.Collectors.toList;

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

    public static List<HttpResponse<String>> performConcurrentRequestsFromListOfLink(List<String> links) throws URISyntaxException {
        List<URI> uris = new ArrayList<URI>();
        for (String link: links) {
            uris.add(new URI(link));
        }
        //https://stackoverflow.com/questions/68711922/java-best-way-to-send-multiple-http-requests
        HttpClient client = HttpClient.newHttpClient();
        List<HttpRequest> requests = uris.stream()
                .map(HttpRequest::newBuilder)
                .map(HttpRequest.Builder::build)
                .toList();

        List<CompletableFuture<HttpResponse<String>>> listOfCompletableFutures = requests.stream()
                .map(request -> client.sendAsync(request, ofString()))
                .toList();

        return listOfCompletableFutures.stream().map(CompletableFuture::join).filter(Objects::nonNull).collect(toList());
    }

}
