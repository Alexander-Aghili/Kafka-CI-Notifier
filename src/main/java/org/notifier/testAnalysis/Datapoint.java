package org.notifier.testAnalysis;

import org.json.JSONObject;
import org.notifier.testAnalysis.OutcomeDistribution;

import javax.xml.crypto.Data;

public class Datapoint implements Comparable<Datapoint> {
    private long startTimestamp;
    private long endTimestamp;
    private OutcomeDistribution outcomeDistribution;

    public Datapoint(JSONObject json) {
        this.startTimestamp = json.getLong("startTimestamp");
        this.endTimestamp = json.getLong("endTimestamp");
        this.outcomeDistribution = new OutcomeDistribution(json.getJSONObject("outcomeDistribution"));
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public OutcomeDistribution getOutcomeDistribution() {
        return outcomeDistribution;
    }

    public void setOutcomeDistribution(OutcomeDistribution outcomeDistribution) {
        this.outcomeDistribution = outcomeDistribution;
    }

    @Override
    public String toString() {
        return "Datapoint{" +
                "startTimestamp=" + startTimestamp +
                ", endTimestamp=" + endTimestamp +
                ", outcomeDistribution=" + outcomeDistribution +
                '}';
    }

    @Override
    public int compareTo(Datapoint o) {
        return Long.compare(this.startTimestamp, o.startTimestamp);
    }
}
