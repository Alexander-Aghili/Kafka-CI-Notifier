package org.notifier.testAnalysis;

import org.json.JSONObject;

public class OutcomeDistribution {
    private int failed;
    private int flaky;
    private int passed;
    private int notSelected;
    private int skipped;
    private int total;

    public OutcomeDistribution(JSONObject outcomeDistribution) {
        this.failed = outcomeDistribution.getInt("failed");
        this.flaky = outcomeDistribution.getInt("flaky");
        this.passed = outcomeDistribution.getInt("passed");
        this.notSelected = outcomeDistribution.getInt("notSelected");
        this.skipped = outcomeDistribution.getInt("skipped");
        this.total = outcomeDistribution.getInt("total");
    }

    // Getters and Setters

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public int getFlaky() {
        return flaky;
    }

    public void setFlaky(int flaky) {
        this.flaky = flaky;
    }

    public int getPassed() {
        return passed;
    }

    public void setPassed(int passed) {
        this.passed = passed;
    }

    public int getNotSelected() {
        return notSelected;
    }

    public void setNotSelected(int notSelected) {
        this.notSelected = notSelected;
    }

    public int getSkipped() {
        return skipped;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }


    @Override
    public String toString() {
        return "OutcomeDistribution{" +
                "failed=" + failed +
                ", flaky=" + flaky +
                ", passed=" + passed +
                ", notSelected=" + notSelected +
                ", skipped=" + skipped +
                ", total=" + total +
                '}';
    }
}