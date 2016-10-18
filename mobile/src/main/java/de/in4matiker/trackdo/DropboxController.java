package de.in4matiker.trackdo;

import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.http.OkHttp3Requestor;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

@EBean(scope = EBean.Scope.Singleton)
public class DropboxController {
    private static final String TAG = DropboxController.class.getSimpleName();

    @Pref
    Preferences_ preferences;

    @StringRes(R.string.app_key)
    String appKey;

    private DbxClientV2 dropboxClient;
    private FullAccount account;
    private MainActivity activity;

    void init() {
        String accessToken = preferences.dropboxToken().get();
        if (accessToken == null || accessToken.isEmpty()) {
            accessToken = Auth.getOAuth2Token();
            if (accessToken != null) {
                preferences.dropboxToken().put(accessToken);
                initAndLoadData(accessToken);
            }
        } else {
            initAndLoadData(accessToken);
        }
    }

    private void initAndLoadData(String accessToken) {
        DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder(DropboxController.class.getName())
                .withHttpRequestor(OkHttp3Requestor.INSTANCE)
                .build();

        dropboxClient = new DbxClientV2(requestConfig, accessToken);
        loadData();
    }

    @Background
    void loadData() {
        try {
            account = dropboxClient.users().getCurrentAccount();
            if (activity != null) {
                activity.updateAccount(account);
            }
        } catch (DbxException e) {
            Log.e(TAG, "Error loading data", e);
        }
    }

    boolean isLoggedIn() {
        return dropboxClient != null && account != null;
    }

    void updateActivity(MainActivity activity) {
        this.activity = activity;
        init();
    }

    void login(MainActivity activity) {
        updateActivity(activity);
        if (!isLoggedIn()) {
            Auth.startOAuth2Authentication(activity, appKey);
        }
    }

    void logout() {
        dropboxClient = null;
        account = null;
        preferences.dropboxToken().put("");
        activity.updateAccount(null);
    }

    @Background
    void getFiles() {
        try {
            ListFolderResult result = dropboxClient.files().listFolder("");
            for (Metadata metadata : result.getEntries()) {
                Log.d(TAG, metadata.getName() + " " + metadata.getPathLower());
            }
        } catch (DbxException e) {
            Log.e(TAG, "Error loading files", e);
        }
    }
}
