package org.notifier.testAnalysis;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Test implements Comparable<Test>{

    private static final String ORIGIN_LINK = "https://ge.apache.org/tests-data/test-history?";
    private static final String UI_LINK = "https://ge.apache.org/scans/tests?";
    private static final String TIME_ZONE = "America/Los_Angeles";
    private static final int DAYS_BEFORE = 90;

    private String dataLink;
    private String UILink;

    private ArrayList<Datapoint> datapoints;

    private double weightedValue; //Used for ranking

    public Test(String container, String test) {
        this.dataLink = convertTestInfoToDataLink(container, test);
        this.UILink = convertTestInfoToUILink(container, test);
    }

    public Test(String container, String test, JSONArray dataPointsJson) throws IOException {
        this.dataLink = convertTestInfoToDataLink(container, test);
        this.UILink = convertTestInfoToUILink(container, test);
        this.datapoints = getDatapointsFromJson(dataPointsJson);
    }

    public Test(String json) throws IOException {
        JSONObject jsonObj = new JSONObject(json);
        String container = jsonObj.getJSONObject("data").getString("container");
        String test = jsonObj.getJSONObject("data").getString("name");
        JSONArray dataPoints = jsonObj.getJSONObject("data").getJSONObject("outcomeTrend").getJSONArray("dataPoints");

        this.dataLink = convertTestInfoToDataLink(container, test);
        this.UILink = convertTestInfoToUILink(container, test);
        this.datapoints = getDatapointsFromJson(dataPoints);
    }

    public String getDataLink() {
        return dataLink;
    }

    public void setDataLink(String dataLink) {
        this.dataLink = dataLink;
    }

    public String getUILink() {
        return UILink;
    }

    public void setUILink(String UILink) {
        this.UILink = UILink;
    }

    public ArrayList<Datapoint> getDatapoints() {
        return datapoints;
    }

    public void setDatapoints(ArrayList<Datapoint> datapoints) {
        this.datapoints = datapoints;
    }

    public double getWeightedValue() {
        return weightedValue;
    }

    public void setWeightedValue(double weightedValue) {
        this.weightedValue = weightedValue;
    }


    private ArrayList<Datapoint> getDatapointsFromJson(JSONArray datapointsJson) throws IOException {
        ArrayList<Datapoint> datapoints = new ArrayList<>();

        if (datapointsJson == null) {
            return datapoints;
        }

        for (Object datapointObj : datapointsJson) {
            JSONObject datapoint = (JSONObject) datapointObj;
            datapoints.add(new Datapoint(datapoint));
        }
        Collections.sort(datapoints);
        return datapoints;
    }

    private String convertTestInfoToUILink(String container, String test) {
        HashMap<String,Object> queryParameters = new HashMap<>();
        queryParameters.put("tests.container", container);
        queryParameters.put("search.relativeStartTime", "P" + String.valueOf(DAYS_BEFORE) + "D");
        queryParameters.put("search.timeZoneId", TIME_ZONE);
        queryParameters.put("tests.test", test);

        return UI_LINK + buildQueryString(queryParameters);
    }

    //Origin link in the form of: https://ge.apache.org/tests-data/test-history?
    private String convertTestInfoToDataLink(String container, String test) {
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

    private String buildQueryString(HashMap<String, Object> parameters) {
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

    private String encodeString(String input) {
        return URLEncoder.encode(input, StandardCharsets.UTF_8);
    }
    private Long getUnixEpochOfMidnightFromDaysBefore(int daysBefore, String timezone) {
        ZonedDateTime today = ZonedDateTime.now(ZoneId.of(timezone));
        ZonedDateTime targetDate = today.minusDays(daysBefore);
        ZonedDateTime targetDateTime = targetDate.with(LocalTime.MIDNIGHT);
        return targetDateTime.toInstant().toEpochMilli();
    }

    @Override
    public int compareTo(Test test) {
        return Double.compare(test.weightedValue, this.weightedValue);
    }


    private double weightingFunction(int position, int total) {
        return (position - (double)(total / 2)) / ((double)total / 2);
    }

    private double valueFunction(Datapoint datapoint) {
        if (datapoint.getOutcomeDistribution().getTotal() == 0) { return 0; }

        double percentFailure = (double) datapoint.getOutcomeDistribution().getFailed() / datapoint.getOutcomeDistribution().getTotal();
        double percentFlaky = (double) datapoint.getOutcomeDistribution().getFlaky() / datapoint.getOutcomeDistribution().getTotal();

        return percentFailure * .5 + percentFlaky * .4;
    }


    public void calculateWeightedValue() {
        for (int i = 0; i < this.datapoints.size(); i++) {
            Datapoint datapoint = this.datapoints.get(i);
            double weight = weightingFunction(i, this.datapoints.size());
            double value = valueFunction(datapoint);
            this.weightedValue += weight*value;
        }
    }

}
