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

import android.app.Application;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.Session.AccessType;

/**
 * 
 * 
 * @author Remko Plantenga
 * 
 */
public class ShopShopViewerApplication extends Application {
    private static ShopShopViewerApplication sInstance;

    final static public String APP_KEY = "iqo7fg98tfzhyb3";
    final static public String APP_SECRET = "r6xtw5tmfqa6kzi";
    final static public AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
    static final String ACCESS_KEY_NAME = "ACCESS_KEY_NAME";
    static final String ACCESS_SECRET_NAME = "ACCESS_SECRET_NAME";

    public enum AppState {
        STARTED, INIT_AUTH, INIT_DROPBOX, DISPLAY
    }

    private AppState appState;

    private Entry currentEntry;

    private DropboxAPI<AndroidAuthSession> dropboxAPI;

    public AppState getAppState() {
        return appState;
    }

    public void setAppState(AppState appState) {
        this.appState = appState;
    }

    public static ShopShopViewerApplication getInstance() {
        return sInstance;
    }

    public static final String APP_NAME = "ShopShopViewer";

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sInstance.initializeInstance();
    }

    protected void initializeInstance() {
    }

    public void setCurrentEntry(Entry e) {
        this.currentEntry = e;
    }

    public Entry getCurrentEntry() {
        return this.currentEntry;
    }

    public void setDropboxAPI(DropboxAPI<AndroidAuthSession> mDBApi) {
        this.dropboxAPI = mDBApi;
    }

    public DropboxAPI<AndroidAuthSession> getDropboxAPI() {
        return this.dropboxAPI;
    }

}
