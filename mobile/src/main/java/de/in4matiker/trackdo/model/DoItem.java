package de.in4matiker.trackdo.model;

import org.joda.time.DateTime;

public abstract class DoItem {
    static final String COMMENT = "[//]: #";
    static final String LIST = "* ";
    static final String INDENT = "\t";
    static final String CREATED = "created:";
    static final String MODIFIED = "modified:";
    static final String REMIND = "remind:";
    static final String DELETED = "deleted:";
    static final String COLOR = "color:";

    String name = "";
    DateTime created;
    DateTime modified;
    DateTime remind;
    DateTime deleted;
    int color;
    String description = "";

    DoItem(String name) {
        this.name = name;
        created = new DateTime();
        modified = new DateTime(created);
    }

    public void setRemind(DateTime remind) {
        this.remind = remind;
    }

    public DateTime getRemind() {
        return remind;
    }

    public boolean hasRemind() {
        return remind != null;
    }

    void modify() {
        modified = new DateTime();
    }

    String getCreatedString() {
        return COMMENT + CREATED + created.toString() + "\n";
    }

    String getModifiedString() {
        return COMMENT + MODIFIED + modified.toString() + "\n";
    }

    String getRemindString() {
        if (remind == null) {
            return "";
        }
        return COMMENT + REMIND + remind.toString() + "\n";
    }

    String getDeletedString() {
        if (deleted == null) {
            return "";
        }
        return COMMENT + DELETED + deleted.toString() + "\n";
    }

    public void setDescription(String description) {
        modify();
        if (description.endsWith("\n")) {
            this.description = description;
        } else {
            this.description = description + "\n";
        }
    }

    public String getDescription() {
        return description;
    }

    public abstract String toString();

    public String getName() {
        return name;
    }
}
