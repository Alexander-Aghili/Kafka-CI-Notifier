package org.notifier;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.notifier.testAnalysis.Datapoint;
import org.notifier.testAnalysis.Test;
import org.notifier.testExtractor.GetTestData;
import org.notifier.testExtractor.GetTestsJson;
import org.notifier.testExtractor.GetTestLinks;
import org.notifier.testExtractor.HTTPRequest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.util.stream.Collectors.toList;

/*
 *  TODO: For project
 *  - Figure out trends
 *  - Figure out how to discover trends
 *  - Notification system
 *  - Storage System
 *  - UI
 */
public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        long startTime = System.currentTimeMillis();

        String jsonData = GetTestsJson.getTestJson();
        ArrayList<String> allLinks = GetTestLinks.getTestDataLinksFromJsonData(jsonData);
        ArrayList<String> links = new ArrayList<>();
        int offset = 0;
        File file = new File("output.txt");
        file.createNewFile();
        for (int j = 0; j < allLinks.size() / 50; j++) {
            for (int i = offset; i < offset + 50; i++) {
                links.add(allLinks.get(i));
            }
            offset = offset + 50;

            System.out.println(links.size());

            List<HttpResponse<String>> responses = HTTPRequest.performConcurrentRequestsFromListOfLink(links);

            ArrayList<Test> tests = new ArrayList<>();
            for (HttpResponse<String> res : responses) {
                Test test = new Test(res.body());
                test.calculateWeightedValue();
                tests.add(test);
                System.out.println(test.getWeightedValue() + ": " + test.getUILink());
            }

            Collections.sort(tests);
            System.out.println(tests.get(0).getUILink());

            FileWriter writer = new FileWriter(file);
            for (Test test: tests) {
                writer.write(String.valueOf(test.getWeightedValue()) + ": " + test.getUILink() + "\n");
            }
            writer.close();
        }


        // Record the end time
        long endTime = System.currentTimeMillis();

        // Calculate the elapsed time in seconds
        long elapsedTime = endTime - startTime;
        double elapsedSeconds = elapsedTime / 1000.0;

        // Print the elapsed time
        System.out.println("Execution time: " + elapsedSeconds + " seconds");

    }
}