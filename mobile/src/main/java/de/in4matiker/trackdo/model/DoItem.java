package de.in4matiker.trackdo.model;

import org.joda.time.DateTime;

public abstract class DoItem {
    static final String COMMENT = "[//]: #";
    static final String INDENT = "\t";

    String name;
    DateTime created;
    DateTime modified;
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
        return COMMENT + "created:" + created.toString() + "\n";
    }

    String getModifiedString() {
        return COMMENT + "modified:" + modified.toString() + "\n";
    }

    String getDeletedString() {
        if (deleted == null) {
            return "";
        }
        return COMMENT + "deleted:" + deleted.toString() + "\n";
    }

    public abstract String toString();
}
