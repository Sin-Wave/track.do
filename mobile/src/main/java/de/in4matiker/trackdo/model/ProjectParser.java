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
    private final Pattern intervals;
    private final Pattern emptyLine;
    private final Pattern listLine;
    private final Pattern intervalLine;
    private final Pattern commentLine;
    private State state = null;

    public ProjectParser() {
        emptyLine = Pattern.compile("^\\s*$");
        title = Pattern.compile("^" + Pattern.quote(DoProject.HEADER.trim()) + "\\s*(.+)$");
        tasks = Pattern.compile(Pattern.quote(DoProject.TASKS.trim()));
        intervals = Pattern.compile(Pattern.quote(DoProject.INTERVALS.trim()));
        listLine = Pattern.compile("^(" + Pattern.quote(DoTask.INDENT) + "?)\\*\\s*(.*)");
        intervalLine = Pattern.compile("^\\*\\s*(\\S*)\\s*(.*)");
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
            Matcher titleMatcher = title.matcher(line);
            Matcher taskMatcher = tasks.matcher(line);
            Matcher intervalMatcher = intervals.matcher(line);
            if (state == null && titleMatcher.matches()) {
                project = new DoProject(titleMatcher.group(1), context);
                state = State.DESCRIPTION;
            } else if (taskMatcher.matches()) {
                state = State.TASKS;
            } else if (intervalMatcher.matches()) {
                state = State.INTERVALS;
            } else if (state == State.DESCRIPTION) {
                Matcher emptyMatch = emptyLine.matcher(line);
                Matcher commentMatcher = commentLine.matcher(line);
                if (emptyMatch.matches()) {
                    if (description.length() > 0) {
                        newLineCount++;
                    }
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
            } else if (state == State.TASKS) {
                Matcher lineMatcher = listLine.matcher(line);
                Matcher commentMatcher = commentLine.matcher(line);
                if (lineMatcher.matches()) {
                    if (lineMatcher.group(1).isEmpty()) {
                        task = project.createTask(lineMatcher.group(2));
                        subTask = null;
                    } else if (task != null) {
                        subTask = task.createChild(lineMatcher.group(2));
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
                } else {
                    if (subTask != null) {
                        subTask.setDescription(subTask.getDescription() + line + "\n");
                    } else if (task != null) {
                        task.setDescription(task.getDescription() + line + "\n");
                    }
                }
            } else if (state == State.INTERVALS) {
                Matcher lineMatcher = intervalLine.matcher(line);
                if (lineMatcher.matches()) {
                    project.addInterval(lineMatcher.group(1), lineMatcher.group(2));
                }
            }
        }
        if (project != null && description.length() > 0) {
            project.setDescription(description.toString());
        }
        return project;
    }

    private enum State {
        DESCRIPTION,
        TASKS,
        INTERVALS,
    }
}
