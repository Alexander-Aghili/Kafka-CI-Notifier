package org.notifier;

import org.notifier.testAnalysis.Datapoint;
import org.notifier.testExtractor.GetTestData;
import org.notifier.testExtractor.GetTestsJson;
import org.notifier.testExtractor.GetTestLinks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/*
 *  TODO: For project
 *  - Get Data
 *  - Figure out trends
 *  - Figure out how to discover trends
 *  - Notification system
 *  - Storage System
 *  - UI
 */
public class Main {
    public static void main(String[] args) throws IOException {
        String jsonData = GetTestsJson.getTestJson();
        ArrayList<String> links = GetTestLinks.getLinksFromJsonData(jsonData);

        for (String link : links) {
            ArrayList<Datapoint> datapoints = GetTestData.getTestDataFromLink(link);
            for (Datapoint d : datapoints) {
                System.out.println(d);
            }
            System.out.println(link);
            return;
        }
    }
}