package de.in4matiker.trackdo.model;

import java.util.ArrayList;
import java.util.List;

public class DoTask extends DoItem {
    private final List<DoTask> subTaskList = new ArrayList<>();
    private DoTask parent;

    public DoTask(String name) {
        super(name);
    }

    public DoTask createChild(String name) {
        modify();
        DoTask child = new DoTask(name);
        child.parent = this;
        subTaskList.add(child);
        return child;
    }

    public void removeChild(DoTask child) {
        modify();
        child.parent = null;
        subTaskList.remove(child);
    }

    private boolean hasParent() {
        return parent != null;
    }

    private DoTask getParent() {
        return parent;
    }

    private String getIndent() {
        if (hasParent()) {
            return parent.getIndent() + INDENT;
        }
        return "";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getIndent());
        sb.append("* ");
        sb.append(name);
        sb.append("\n");
        if (!description.isEmpty()) {
            for (String line : description.split("\n")) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                sb.append(getIndent());
                sb.append(line.trim());
                sb.append("\n");
            }
        }
        if (hasRemind()) {
            sb.append(getRemindString());
        }
        for (DoTask task : subTaskList) {
            sb.append(task.toString());
        }
        return sb.toString();
    }
}
