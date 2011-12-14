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

import java.io.File;
import java.io.FilenameFilter;

import android.app.Application;
import android.os.Environment;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.Session.AccessType;

/**
 * Object that holds global data and state of the application.
 * 
 * @author Remko Plantenga
 * 
 */
public class ShopShopViewerApplication extends Application {

    private static ShopShopViewerApplication sInstance;
    private static final String SHOPSHOP_EXTENSION = ".shopshop";

    public static final AccessType ACCESS_TYPE = AccessType.DROPBOX;
    public static final String APP_NAME = "ShopShopViewer";
    public static final String DROPBOX_FOLDER = "/ShopShop";

    public enum AppState {
        STARTED, INIT_AUTH, INIT_DROPBOX, DISPLAY, AUTH_SUCCESS, SWITCH_TO_DROPBOX, SYNCHRONIZE, WAITING
    }

    private AppState appState;
    private String currentFile;
    private DropboxAPI<AndroidAuthSession> dropboxAPI;

    private boolean externalStorageAvailable;
    private boolean externalStorageWriteable;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sInstance.initializeInstance();
    }

    public static ShopShopViewerApplication getInstance() {
        return sInstance;
    }

    protected void initializeInstance() {
        this.checkExternalStorageAvailable();
    }

    public AppState getAppState() {
        return this.appState;
    }

    public void setAppState(AppState appState) {
        this.appState = appState;
    }

    public void setCurrentFile(String s) {
        this.currentFile = s;
    }

    public String getCurrentFile() {
        return this.currentFile;
    }

    public void setDropboxAPI(DropboxAPI<AndroidAuthSession> mDBApi) {
        this.dropboxAPI = mDBApi;
    }

    public DropboxAPI<AndroidAuthSession> getDropboxAPI() {
        return this.dropboxAPI;
    }

    public boolean isExternalStorageAvailable() {
        return this.externalStorageAvailable;
    }

    public boolean isExternalStorageWriteable() {
        return this.externalStorageWriteable;
    }

    public void checkExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            externalStorageAvailable = externalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            externalStorageAvailable = true;
            externalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but
            // all we need
            // to know is we can neither read nor write
            externalStorageAvailable = externalStorageWriteable = false;
        }
    }

    public String[] getFiles() {
        File appFolder = getExternalFilesDir(null);
        return appFolder.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(SHOPSHOP_EXTENSION)) {
                    return true;
                }
                return false;
            }
        });
    }

}
