package de.in4matiker.trackdo.model;

import org.joda.time.Interval;

/**
 * @author Julian Köpke <julian.koepke@jambit.com>
 * @since 25.10.16
 */

public class DoInterval {
    private String description;
    private Interval interval;

    public DoInterval(Interval interval, String description) {
        this.interval = interval;
        if (description != null && !description.isEmpty()) {
            this.description = description;
        }
    }

    public DoInterval(Interval interval) {
        this(interval, null);
    }

    public DoInterval(String interval, String description) {
        this(Interval.parse(interval), description);
    }

    public DoInterval(String interval) {
        this(interval, null);
    }


    public boolean hasDescription() {
        return description != null;
    }

    public Interval getInterval() {
        return interval;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(DoProject.LIST);
        sb.append(interval.toString());
        if (hasDescription()) {
            sb.append(" ");
            sb.append(description);
        }
        return sb.toString();
    }
}
