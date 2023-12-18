package org.notifier;

import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.jetbrains.annotations.NotNull;
import org.notifier.testAnalysis.Datapoint;
import org.notifier.testAnalysis.Test;
import org.notifier.testExtractor.GetTestData;
import org.notifier.testExtractor.GetTestsJson;
import org.notifier.testExtractor.GetTestLinks;
import org.notifier.testExtractor.HTTPRequest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.*;

/*
 *  TODO: For project
 *  - Figure out trends
 *  - Figure out how to discover trends
 *  - Notification system
 *  - Storage System
 *  - UI
 */
public class Main {
    public static void main(String[] args) throws IOException {
        String jsonData = GetTestsJson.getTestJson();
        ArrayList<Test> tests = GetTestLinks.getTestsFromJsonData(jsonData);
        ArrayList<Test> temp = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            temp.add(tests.get(i));
        }
        tests = temp;
        // Create a ThreadPoolExecutor with a maximum of 500 threads and infinite keep-alive time
        ExecutorService executor = new ThreadPoolExecutor(
                50,  // corePoolSize
                50, // maximumPoolSize
                Long.MAX_VALUE, // keepAliveTime set to Long.MAX_VALUE for infinite time
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );

        System.out.println(tests.size());
        for (Test test : tests) {
            String link = test.getDataLink();
            System.out.println(test.getUILink());
            ListenableFuture<Response> future = HTTPRequest.asyncHttpRequest(link);
            Runnable callback = () -> {
                try {
                    Response response = future.get();
                    System.out.println(response.getResponseBody());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            future.addListener(callback, executor);

//            test.setDatapoints(GetTestData.getTestDataFromLink(link));
//            test.calculateWeightedValue();
//            System.out.println(test.getWeightedValue() + ": " + test.getUILink());

        }


        Collections.sort(tests);
        System.out.println(tests.get(0).getUILink());
    }
}