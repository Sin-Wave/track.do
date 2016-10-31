package de.in4matiker.trackdo.model;

import java.util.ArrayList;
import java.util.List;

public class DoContext extends DoItem {
    static final String HEADER = "# ";
    private final List<DoProject> projectList = new ArrayList<>();

    public DoContext(String name) {
        super(name);
    }

    public void removeProject(DoProject project) {
        modify();
        project.context = null;
        projectList.remove(project);
    }

    public void addProject(DoProject project) {
        modify();
        project.context = this;
        projectList.add(project);
    }

    public List<DoProject> getProjects() {
        return projectList;
    }

    public DoProject createProject(String name) {
        modify();
        DoProject project = new DoProject(name, this);
        projectList.add(project);
        return project;
    }

    @Override
    public String toString() {
        return HEADER + name +
                "\n\n" +
                getDescription() +
                getCreatedString() +
                getModifiedString() +
                getRemindString() +
                getDeletedString();
    }
}
