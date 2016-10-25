package de.in4matiker.trackdo.model;

import android.graphics.Color;

import org.joda.time.Duration;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;

public class DoProject extends DoItem {
    static final String HEADER = "## ";
    static final String TASKS = "\n### Tasks\n\n";
    static final String INTERVALS = "\n### Time track\n\n";
    private String description = "";
    private final List<DoTask> taskList = new ArrayList<>();
    private DoContext context;
    private int color;
    private List<Interval> intervals;

    public DoProject(String name, DoContext context) {
        super(name);
        this.context = context;
        intervals = new ArrayList<>();
    }

    void setContext(DoContext context) {
        modify();
        if (this.context != null) {
            this.context.removeProject(this);
        }
        this.context = context;
    }

    public void setDescription(String description) {
        modify();
        this.description = description;
    }

    public int getColor() {
        return color;
    }

    public void setColor(String color) {
        setColor(Color.parseColor(color));
    }

    public void setColor(int color) {
        modify();
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public DoTask createTask(String name) {
        modify();
        DoTask task = new DoTask(name);
        taskList.add(task);
        return task;
    }

    void addInterval(String interval) {
        intervals.add(Interval.parse(interval));
    }

    Duration getTime() {
        Duration duration = new Duration(0);
        for (Interval interval : intervals) {
            duration = duration.plus(interval.toDuration());
        }
        return duration;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(HEADER);
        sb.append(name);
        sb.append("\n");
        if (description != null && !description.isEmpty()) {
            sb.append("\n");
            sb.append(description);
        }
        sb.append("\n");
        sb.append(getCreatedString());
        sb.append(getModifiedString());
        sb.append(getRemindString());
        sb.append(getDeletedString());
        if (color >>> 24 > 0) {
            sb.append(COMMENT);
            sb.append(COLOR + "#");
            sb.append(Integer.toHexString(color));
            sb.append("\n");
        }
        sb.append(TASKS);
        for (DoTask task : taskList) {
            sb.append(task.toString());
        }
        sb.append(INTERVALS);
        for (Interval interval : intervals) {
            sb.append(LIST);
            sb.append(interval.toString());
            sb.append("\n");
        }

        sb.append("\n");
        return sb.toString();
    }
}
