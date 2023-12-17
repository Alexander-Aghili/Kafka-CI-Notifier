package org.notifier;

import org.notifier.testExtractor.GetTestsJson;
import org.notifier.testExtractor.GetTestLinks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        String jsonData = GetTestsJson.getTestJson();
        ArrayList<String> links = GetTestLinks.getLinksFromJsonData(jsonData);

        // Specify the file path where you want to write the links
        String filePath = "/tmp/kafka/links.txt";
        // Create the file if it doesn't exist
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        if (file.createNewFile()) {
            System.out.println("File created: " + file.getAbsolutePath());
        }

        FileWriter writer = new FileWriter(file);
        for (String link : links) {
            System.out.println(link);
        }
        System.out.println("Links have been written to the file: " + filePath);
    }
}