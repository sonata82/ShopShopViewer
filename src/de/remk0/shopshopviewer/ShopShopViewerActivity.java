package de.remk0.shopshopviewer;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

import de.remk0.shopshopviewer.ShopShopViewerApplication.AppState;

/**
 * 
 * @author Remko Plantenga
 * 
 */
public class ShopShopViewerActivity extends ListActivity {
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private ShopShopViewerApplication application = (ShopShopViewerApplication) getApplicationContext();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application.setAppState(AppState.STARTED);

        AndroidAuthSession session = buildSession();
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

        // setContentView(R.layout.main);
        //

        application.setAppState(AppState.INIT_AUTH);
        mDBApi.getSession().startAuthentication(ShopShopViewerActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // MANDATORY call to complete auth.
                // Sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                AccessTokenPair tokens = mDBApi.getSession()
                        .getAccessTokenPair();

                // Provide your own storeKeys to persist the access token pair
                // A typical way to store tokens is using SharedPreferences
                storeKeys(tokens.key, tokens.secret);

                application.setAppState(AppState.INIT_DROPBOX);

                DropboxAPI.Entry dbe = null;
                try {
                    dbe = mDBApi.metadata("/", 10000, null, true, null);
                } catch (DropboxException e) {
                    Log.e(ShopShopViewerApplication.APP_NAME,
                            "Error retrieving folder", e);
                }

                if (dbe != null) {
                    setListAdapter(new ArrayAdapter<Entry>(this,
                            R.layout.list_item, dbe.contents));
                }
            } catch (IllegalStateException e) {
                Log.i(ShopShopViewerApplication.APP_NAME,
                        "Error authenticating", e);
            }
        }
    }

    private String[] getKeys() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String key = prefs.getString(ShopShopViewerApplication.ACCESS_KEY_NAME,
                null);
        String secret = prefs.getString(
                ShopShopViewerApplication.ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
            String[] ret = new String[2];
            ret[0] = key;
            ret[1] = secret;
            return ret;
        } else {
            return null;
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a
     * local store, rather than storing user name & password, and
     * re-authenticating each time (which is not to be done, ever).
     */
    private void storeKeys(String key, String secret) {
        // Save the access key for later
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Editor edit = prefs.edit();
        edit.putString(ShopShopViewerApplication.ACCESS_KEY_NAME, key);
        edit.putString(ShopShopViewerApplication.ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

    private void clearKeys() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(
                ShopShopViewerApplication.APP_KEY,
                ShopShopViewerApplication.APP_SECRET);
        AndroidAuthSession session;

        String[] stored = getKeys();
        if (stored != null) {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0],
                    stored[1]);
            session = new AndroidAuthSession(appKeyPair,
                    ShopShopViewerApplication.ACCESS_TYPE, accessToken);
        } else {
            session = new AndroidAuthSession(appKeyPair,
                    ShopShopViewerApplication.ACCESS_TYPE);
        }

        return session;
    }
}