package de.in4matiker.trackdo.model;

import java.util.ArrayList;
import java.util.List;

public class DoContext extends DoItem {
    private final List<DoProject> projectList = new ArrayList<>();

    public DoContext(String name) {
        super(name);
    }

    public void removeProject(DoProject project) {
        modify();
        project.setContext(null);
        projectList.remove(project);
    }

    public void addProject(DoProject project) {
        modify();
        project.setContext(this);
        projectList.add(project);
    }

    public DoProject createProject(String name) {
        modify();
        DoProject project = new DoProject(name, this);
        projectList.add(project);
        return project;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("# ");
        sb.append(name);
        sb.append("\n\n");
        sb.append(getCreatedString());
        sb.append(getModifiedString());
        sb.append(getRemindString());
        sb.append(getDeletedString());
        for (DoProject project : projectList) {
            sb.append("\n");
            sb.append(project.toString());
        }
        return sb.toString();
    }
}
