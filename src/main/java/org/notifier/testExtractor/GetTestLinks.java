package org.notifier.testExtractor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.notifier.testAnalysis.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import java.util.HashMap;
public class GetTestLinks {

    public static ArrayList<Test> getTestsFromJsonData(String jsonData) throws JsonProcessingException {
        ArrayList<Test> tests = new ArrayList<>();

        // Parse JSON using Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonData);

        for (JsonNode jsonNode : rootNode) {
            String container = jsonNode.get("suiteName").asText();
            String testName = jsonNode.get("displayName").asText();
            Test test = new Test(container, testName);
            tests.add(test);
        }

        return tests;
    }
}
