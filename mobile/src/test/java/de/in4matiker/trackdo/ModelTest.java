package de.in4matiker.trackdo;

import android.graphics.Color;

import org.joda.time.DateTime;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import de.in4matiker.trackdo.model.DoContext;
import de.in4matiker.trackdo.model.DoProject;
import de.in4matiker.trackdo.model.DoTask;
import de.in4matiker.trackdo.model.ProjectParser;

public class ModelTest {
    @Test
    public void testString() throws Exception {
        DoContext context = new DoContext("Context");
        context.createProject("Some project").setColor(0xffff00ff);
        context.createProject("Another project");
        DoProject project = context.createProject("Project");
        project.setRemind(new DateTime().plusWeeks(1));
        project.setColor(Color.RED);
        project.setDescription("This is a project description\nIt contains newlines\n\nThere are also empty lines\n");
        DoTask task = project.createTask("Task with subtasks");
        task.createChild("Subtask a");
        task.createChild("Subtask b").setRemind(new DateTime().plusHours(1));
        task.createChild("Subtask c");
        task.setRemind(new DateTime().plusDays(1));
        for (int i = 1; i < 5; i++) {
            project.createTask("Task " + i);
        }
        System.out.println(context.toString());
        System.out.println("---\n");
        ProjectParser parser = new ProjectParser();
        BufferedReader buffer = new BufferedReader(new StringReader(project.toString()));
        System.out.println(parser.read(context, buffer).toString());
    }
}