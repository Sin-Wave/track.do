package de.in4matiker.trackdo.model;

import org.joda.time.DateTime;

public abstract class DoItem {
    static final String COMMENT = "[//]: #";
    static final String INDENT = "\t";
    static final String CREATED = "created:";
    static final String MODIFIED = "modified:";
    static final String REMIND = "remind:";
    static final String DELETED = "deleted:";
    static final String COLOR = "color:";

    String name;
    DateTime created;
    DateTime modified;
    DateTime remind;
    DateTime deleted;

    DoItem(String name) {
        this.name = name;
        created = new DateTime();
        modified = new DateTime(created);
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

    public abstract String toString();
}
