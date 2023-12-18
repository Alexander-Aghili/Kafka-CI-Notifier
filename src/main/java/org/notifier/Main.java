package org.notifier;

import org.notifier.testAnalysis.Datapoint;
import org.notifier.testAnalysis.Test;
import org.notifier.testExtractor.GetTestData;
import org.notifier.testExtractor.GetTestsJson;
import org.notifier.testExtractor.GetTestLinks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ForkJoinPool;

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
        File file = new File("/tmp/kafka/output.txt");
        file.getParentFile().mkdirs();
        file.createNewFile();
        FileWriter tempWriter = new FileWriter(file);

        ForkJoinPool myPool = new ForkJoinPool(25);
        myPool.submit(() -> {
            tests.parallelStream().forEach((test) -> {
                String link = test.getDataLink();
                try {
                    test.setDatapoints(new GetTestData().getTestDataFromLink(link));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                test.calculateWeightedValue();
                System.out.println(test.getWeightedValue() + ": " + test.getUILink());
                try {
                    tempWriter.write(test.getWeightedValue() + ": " + test.getUILink() + "\n");
                    tempWriter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });
        }).join();

        tempWriter.close();
        Collections.sort(tests);
        System.out.println(tests.get(0).getUILink());

        File finalFile = new File("/tmp/kafka/final_output.txt");
        finalFile.getParentFile().mkdirs();
        finalFile.createNewFile();
        FileWriter writer = new FileWriter(finalFile);

        for (Test test: tests) {
            writer.write(test.getUILink() + " " + String.valueOf(test.getWeightedValue()) + "\n");
        }
        writer.close();
    }
}