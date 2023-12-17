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

        tests.parallelStream().forEach((test) -> {
            String link = test.getDataLink();
            try {
                test.setDatapoints(new GetTestData().getTestDataFromLink(link));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            test.calculateWeightedValue();
            System.out.println(test.getWeightedValue() + ": " + test.getUILink());
        });
        Collections.sort(tests);
        System.out.println(tests.get(0).getUILink());
        File file = new File("output.txt");
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        for (Test test: tests) {
            writer.write(test.getUILink() + " " + String.valueOf(test.getWeightedValue()) + "\n");
        }
        writer.close();
    }
}