package de.in4matiker.trackdo.model;

import org.androidannotations.annotations.EBean;
import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Julian Köpke <julian.koepke@jambit.com>
 * @since 13.10.16
 */
@EBean(scope = EBean.Scope.Singleton)
public class ProjectParser {
    private final Pattern title;
    private final Pattern tasks;
    private final Pattern emptyLine;
    private final Pattern taskLine;
    private final Pattern commentLine;
    private State state = State.TITLE;

    public ProjectParser() {
        title = Pattern.compile("^" + Pattern.quote(DoProject.HEADER) + "\\s*(.+)$");
        emptyLine = Pattern.compile("^\\s*$");
        tasks = Pattern.compile(Pattern.quote(DoProject.TASKS));
        taskLine = Pattern.compile("^(" + Pattern.quote(DoTask.INDENT) + "?)\\*\\s*(.*)");
        commentLine = Pattern.compile("^" + Pattern.quote(DoItem.COMMENT) + "(.*)");
    }

    @SuppressWarnings("ConstantConditions")
    public DoProject read(DoContext context, BufferedReader reader) throws IOException {
        String line;
        DoProject project = null;
        DoTask task = null;
        DoTask subTask = null;
        int newLineCount = 0;
        StringBuilder description = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if (state == State.TITLE) {
                Matcher matcher = title.matcher(line);
                if (matcher.matches()) {
                    project = new DoProject(matcher.group(1), context);
                    state = State.DESCRIPTION;
                }
            } else if (state == State.DESCRIPTION) {
                Matcher emptyMatch = emptyLine.matcher(line);
                Matcher taskMatch = tasks.matcher(line);
                Matcher commentMatcher = commentLine.matcher(line);
                if (emptyMatch.matches()) {
                    if (description.length() > 0) {
                        newLineCount++;
                    }
                } else if (taskMatch.matches()) {
                    state = State.TASK;
                } else if (commentMatcher.matches()) {
                    String comment = commentMatcher.group(1);
                    if (comment.startsWith(DoItem.CREATED)) {
                        project.created = new DateTime(comment.substring(DoItem.CREATED.length()));
                    } else if (comment.startsWith(DoItem.MODIFIED)) {
                        project.modified = new DateTime(comment.substring(DoItem.MODIFIED.length()));
                    } else if (comment.startsWith(DoItem.DELETED)) {
                        project.deleted = new DateTime(comment.substring(DoItem.DELETED.length()));
                    } else if (comment.startsWith(DoItem.COLOR)) {
//                        project.setColor(comment.substring(DoItem.COLOR.length()));
                    } else if (comment.startsWith(DoItem.REMIND)) {
                        DateTime date = new DateTime(comment.substring(DoItem.REMIND.length()));
                        project.setRemind(date);
                    }
                } else {
                    for (int i = 0; i < newLineCount; i++) {
                        description.append("\n");
                    }
                    newLineCount = 0;
                    description.append(line);
                    description.append("\n");
                }
            } else if (state == State.TASK) {
                Matcher taskMatcher = taskLine.matcher(line);
                Matcher commentMatcher = commentLine.matcher(line);
                if (taskMatcher.matches()) {
                    if (taskMatcher.group(1).isEmpty()) {
                        task = project.createTask(taskMatcher.group(2));
                        subTask = null;
                    } else if (task != null) {
                        subTask = task.createChild(taskMatcher.group(2));
                    }
                } else if (commentMatcher.matches()) {
                    String comment = commentMatcher.group(1);
                    if (comment.startsWith(DoItem.REMIND)) {
                        DateTime date = new DateTime(comment.substring(DoItem.REMIND.length()));
                        if (subTask != null) {
                            subTask.setRemind(date);
                        } else if (task != null) {
                            task.setRemind(date);
                        }
                    }
                }
            }
        }
        if (project != null && description.length() > 0) {
            project.setDescription(description.toString());
        }
        return project;
    }

    private enum State {
        TITLE,
        DESCRIPTION,
        TASK,
    }
}
