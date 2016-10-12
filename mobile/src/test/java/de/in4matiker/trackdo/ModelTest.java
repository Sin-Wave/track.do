package de.in4matiker.trackdo;

import android.graphics.Color;

import org.junit.Test;

import de.in4matiker.trackdo.model.DoContext;
import de.in4matiker.trackdo.model.DoProject;
import de.in4matiker.trackdo.model.DoTask;

public class ModelTest {
    @Test
    public void testString() throws Exception {
        DoContext context = new DoContext("Context");
        context.createProject("Some project").setColor(0xffff00ff);
        context.createProject("Another project");
        DoProject project = context.createProject("Project");
        project.setColor(Color.RED);
        project.setDescription("This is a project description\nIt contains newlines");
        DoTask task = project.createTask("Task with subtasks");
        task.createChild("Subtask a");
        task.createChild("Subtask b");
        task.createChild("Subtask c");
        for (int i = 1; i < 5; i++) {
            project.createTask("Task " + i);
        }
        System.out.println(context.toString());
        System.out.println("---\n");
        System.out.println(project.toString());
    }
}