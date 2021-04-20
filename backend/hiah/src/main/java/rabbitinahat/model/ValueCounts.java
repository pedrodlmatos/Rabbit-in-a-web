/* Adapted from ValueCounts (rabbit-core) */
package rabbitinahat.model;

import java.util.ArrayList;

public class ValueCounts {

    private ArrayList<ValueCounts.ValueCount> valueCounts = new ArrayList<>();
    private int totalFrequency = 0;
    private String mostFrequentValue;
    private int mostFrequentValueCount = -1;

    public class ValueCount {

        private String value;
        private int frequency;

        public ValueCount(String value, int frequency) {
            this.value = value;
            this.frequency = frequency;
        }


        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }
    }

    public boolean add(String value, int count) {
        totalFrequency += count;
        if (count > mostFrequentValueCount) {
            mostFrequentValue = value;
            mostFrequentValueCount = count;
        }
        return valueCounts.add(new ValueCount(value, count));
    }

    public ArrayList<ValueCounts.ValueCount> getAll() {
        return valueCounts;
    }

    public ValueCounts.ValueCount get(int i) {
        return valueCounts.get(i);
    }

    public String getMostFrequentValue() {
        return mostFrequentValue;
    }

    public int getTotalFrequency() {
        return totalFrequency;
    }

    public int size() {
        return valueCounts.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }
}
