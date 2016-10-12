package de.in4matiker.trackdo.model;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class DoProject extends DoItem {
    private String description;
    private final List<DoTask> taskList = new ArrayList<>();
    private DoContext context;
    private int color;

    public DoProject(String name, DoContext context) {
        super(name);
        this.context = context;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("## ");
        sb.append(name);
        sb.append("\n");
        if (description != null && !description.isEmpty()) {
            sb.append("\n");
            sb.append(description);
            sb.append("\n");
        }
        for (DoTask task : taskList) {
            sb.append("\n");
            sb.append(task.toString());
        }
        if (!taskList.isEmpty()) {
            sb.append("\n");
        }
        sb.append("\n");
        sb.append(getCreatedString());
        sb.append(getModifiedString());
        sb.append(getDeletedString());
        if (color >>> 24 > 0) {
            sb.append(COMMENT);
            sb.append("color:#");
            sb.append(Integer.toHexString(color));
            sb.append("\n");
        }
        return sb.toString();
    }
}
