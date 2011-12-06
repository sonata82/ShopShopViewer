package de.remk0.shopshopviewer;

import android.app.Application;

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
        STARTED, INIT_AUTH, INIT_DROPBOX
    }

    private AppState appState;

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

}
