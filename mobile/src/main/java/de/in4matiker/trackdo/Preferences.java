package de.in4matiker.trackdo;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface Preferences {
    @DefaultString(keyRes = R.string.dropbox_token, value = "")
    String dropboxToken();
}
