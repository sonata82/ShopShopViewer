/**
 * ShopShopViewer
 * Copyright (C) 2011 Remko Plantenga
 * 
 * This file is part of ShopShopViewer.
 *
 * ShopShopViewer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ShopShopViewer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ShopShopViewer. If not, see <http://www.gnu.org/licenses/>.
 */
package de.remk0.shopshopviewer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

import de.remk0.shopshopviewer.ShopShopViewerApplication.AppState;

/**
 * This activity forces the user to logon to a Dropbox account and on success
 * saves the session in the global application object. Most of the code here is
 * directly from the Dropbox tutorial and/or demo.
 * 
 * @author Remko Plantenga
 * 
 */
public class DropboxAuthActivity extends Activity {

    private static final String ACCESS_KEY_NAME = "ACCESS_KEY_NAME";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET_NAME";
    private static final AccessType ACCESS_TYPE = AccessType.DROPBOX;

    private String APP_KEY;
    private String APP_SECRET;

    private ShopShopViewerApplication application;
    private DropboxAPI<AndroidAuthSession> mDBApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        APP_KEY = getString(R.string.app_key);
        APP_SECRET = getString(R.string.app_secret);

        application = (ShopShopViewerApplication) getApplicationContext();
        application.setAppState(AppState.INIT_AUTH);

        AndroidAuthSession session = buildSession();
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

        mDBApi.getSession().startAuthentication(DropboxAuthActivity.this);

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

                application.setDropboxAPI(mDBApi);
                application.setAppState(AppState.AUTH_SUCCESS);

                setResult(RESULT_OK);
                finish();
            } catch (IllegalStateException e) {
                Log.e(ShopShopViewerApplication.APP_NAME,
                        "Error authenticating", e);
                setResult(RESULT_CANCELED);
                finish();
            }
        } else {
            if (application.getAppState() == AppState.INIT_AUTH) {
                application.setAppState(AppState.SWITCH_TO_DROPBOX);
            } else {
                Log.e(ShopShopViewerApplication.APP_NAME,
                        "Failed connecting to Dropbox");
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    private String[] getKeys() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String key = prefs.getString(DropboxAuthActivity.ACCESS_KEY_NAME, null);
        String secret = prefs.getString(DropboxAuthActivity.ACCESS_SECRET_NAME,
                null);
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
        edit.putString(DropboxAuthActivity.ACCESS_KEY_NAME, key);
        edit.putString(DropboxAuthActivity.ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

    private void clearKeys() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Editor edit = prefs.edit();
        edit.remove(ACCESS_KEY_NAME);
        edit.remove(ACCESS_SECRET_NAME);
        edit.commit();
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(this.APP_KEY, this.APP_SECRET);
        AndroidAuthSession session;

        String[] stored = getKeys();
        if (stored != null) {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0],
                    stored[1]);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE,
                    accessToken);
        } else {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }

        return session;
    }

}
