package org.notifier.testExtractor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

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

    private static final String ORIGIN_LINK = "https://ge.apache.org/tests-data/test-history?";
    private static final String TIME_ZONE = "America/Los_Angeles";
    private static final int DAYS_BEFORE = 90;


    public static ArrayList<String> getLinksFromJsonData(String jsonData) throws JsonProcessingException {
        ArrayList<String> links = new ArrayList<>();

        // Parse JSON using Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonData);

        for (JsonNode jsonNode : rootNode) {
            String container = jsonNode.get("suiteName").asText();
            String test = jsonNode.get("displayName").asText();
            String link = convertTestInfoToDataLink(container, test);
            links.add(link);
        }

        return links;
    }

    //Origin link in the form of: https://ge.apache.org/tests-data/test-history?
    private static String convertTestInfoToDataLink(String container, String test) {
        HashMap<String, Object> queryParameters = new HashMap<>();
        queryParameters.put("container", container);
        queryParameters.put("startTimeMin", getUnixEpochOfMidnightFromDaysBefore(DAYS_BEFORE, TIME_ZONE));
        queryParameters.put("startTimeMax", Instant.now().toEpochMilli());
        queryParameters.put("sortField", "FAILED");
        queryParameters.put("test", test);
        queryParameters.put("timeZoneId", TIME_ZONE);
        queryParameters.put("unstableOnly", "false");

        return ORIGIN_LINK + buildQueryString(queryParameters);
    }

    private static String buildQueryString(HashMap<String, Object> parameters) {
        StringBuilder queryString = new StringBuilder();
        for (HashMap.Entry<String, Object> entry : parameters.entrySet()) {
            if (!queryString.isEmpty()) {
                queryString.append("&");
            }
            queryString.append(encodeString(entry.getKey()))
                    .append("=")
                    .append(encodeString(String.valueOf(entry.getValue())));
        }
        return queryString.toString();
    }

    private static String encodeString(String input) {
        return URLEncoder.encode(input, StandardCharsets.UTF_8);
    }
    private static Long getUnixEpochOfMidnightFromDaysBefore(int daysBefore, String timezone) {
        ZonedDateTime today = ZonedDateTime.now(ZoneId.of(timezone));
        ZonedDateTime targetDate = today.minusDays(daysBefore);
        ZonedDateTime targetDateTime = targetDate.with(LocalTime.MIDNIGHT);
        return targetDateTime.toInstant().toEpochMilli();
    }


}
