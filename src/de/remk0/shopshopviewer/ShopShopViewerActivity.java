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

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;
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
    private ShopShopViewerApplication application;
    private String hash = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (ShopShopViewerApplication) getApplicationContext();
        application.setAppState(AppState.STARTED);

        AndroidAuthSession session = buildSession();
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

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
                String tempHash = hash;
                try {
                    dbe = mDBApi.metadata("/", 10000, tempHash, true, null);
                } catch (DropboxServerException e) {
                    switch (e.error) {
                    case 304:
                        if (tempHash != null) {
                            Log.d(ShopShopViewerApplication.APP_NAME,
                                    "Folder has not changed since last request");
                            break;
                        }
                    default:
                        Log.e(ShopShopViewerApplication.APP_NAME,
                                "Error retrieving folders", e);
                        break;
                    }
                } catch (DropboxException e) {
                    Log.e(ShopShopViewerApplication.APP_NAME,
                            "Error retrieving folder", e);
                }

                if (dbe != null) {
                    this.hash = dbe.hash;
                    setListAdapter(new ListOfDropBoxEntriesAdapter<Entry>(this,
                            R.layout.list_item, dbe.contents));
                } else {
                    Log.d(ShopShopViewerApplication.APP_NAME,
                            "Returned empty DropBoxEntry-Object");
                }
            } catch (IllegalStateException e) {
                Log.i(ShopShopViewerApplication.APP_NAME,
                        "Error authenticating", e);
            }
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);

        Entry e = (Entry) this.getListAdapter().getItem(position);
        this.application.setCurrentEntry(e);
        this.application.setDropboxAPI(mDBApi);
        startActivity(new Intent(this, DisplayFileActivity.class));
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