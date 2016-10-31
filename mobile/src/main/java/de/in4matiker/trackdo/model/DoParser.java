package de.in4matiker.trackdo.model;

import android.util.Log;

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
public class DoParser {
    private static final String TAG = DoParser.class.getSimpleName();
    private final Pattern contextTitle;
    private final Pattern taskTitle;
    private final Pattern tasks;
    private final Pattern intervals;
    private final Pattern emptyLine;
    private final Pattern listLine;
    private final Pattern intervalLine;
    private final Pattern commentLine;
    private State state = null;

    public DoParser() {
        emptyLine = Pattern.compile("^\\s*$");
        contextTitle = Pattern.compile("^" + Pattern.quote(DoContext.HEADER.trim()) + "\\s*(.+)$");
        taskTitle = Pattern.compile("^" + Pattern.quote(DoProject.HEADER.trim()) + "\\s*(.+)$");
        tasks = Pattern.compile(Pattern.quote(DoProject.TASKS.trim()));
        intervals = Pattern.compile(Pattern.quote(DoProject.INTERVALS.trim()));
        listLine = Pattern.compile("^(" + Pattern.quote(DoTask.INDENT) + "?)\\*\\s*(.*)");
        intervalLine = Pattern.compile("^\\*\\s*(\\S*)\\s*(.*)");
        commentLine = Pattern.compile("^" + Pattern.quote(DoItem.COMMENT) + "(.*)");
    }

    public DoContext readContext(String name, BufferedReader reader) {
        DoContext context = null;
        String line;
        StringBuilder description = new StringBuilder();
        try {
            while ((line = reader.readLine()) != null) {
                Matcher titleMatcher = contextTitle.matcher(line);
                Matcher comment = commentLine.matcher(line);
                if (titleMatcher.matches()) {
                    context = new DoContext(titleMatcher.group(1));
                } else if (comment.matches()) {
                    setComment(context, comment);
                } else {
                    description.append(line);
                    description.append("\n");
                }
            }
        } catch (IOException e) {
            Log.w(TAG, "Error reading context description " + name, e);
        }
        if (context != null) {
            context.setDescription(description.toString());
        }
        return context;
    }

    @SuppressWarnings("ConstantConditions")
    public DoProject readProject(DoContext context, BufferedReader reader) {
        String line;
        DoProject project = null;
        DoTask task = null;
        DoTask subTask = null;
        int newLineCount = 0;
        StringBuilder description = new StringBuilder();
        try {
            while ((line = reader.readLine()) != null) {
                Matcher titleMatcher = taskTitle.matcher(line);
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
                        setComment(project, commentMatcher);
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
                        if (subTask != null) {
                            setComment(subTask, commentMatcher);
                        } else if (task != null) {
                            setComment(task, commentMatcher);
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
        } catch (IOException e) {
            Log.w(TAG, "Error reading project file in context " + context.getDescription(), e);
        }
        if (project != null && description.length() > 0) {
            project.setDescription(description.toString());
        }
        return project;
    }

    private void setComment(DoItem item, Matcher commentMatcher) {
        String comment = commentMatcher.group(1);
        if (comment.startsWith(DoItem.CREATED)) {
            item.created = new DateTime(comment.substring(DoItem.CREATED.length()));
        } else if (comment.startsWith(DoItem.MODIFIED)) {
            item.modified = new DateTime(comment.substring(DoItem.MODIFIED.length()));
        } else if (comment.startsWith(DoItem.DELETED)) {
            item.deleted = new DateTime(comment.substring(DoItem.DELETED.length()));
        } else if (comment.startsWith(DoItem.COLOR)) {
            String color = comment.substring(DoItem.COLOR.length()).trim();
            if (color.startsWith("#")) {
                color = color.substring(1);
            }
            item.color = Integer.getInteger(color, 16);
        } else if (comment.startsWith(DoItem.REMIND)) {
            DateTime date = new DateTime(comment.substring(DoItem.REMIND.length()));
            item.setRemind(date);
        }

    }

    private enum State {
        DESCRIPTION,
        TASKS,
        INTERVALS,
    }
}
