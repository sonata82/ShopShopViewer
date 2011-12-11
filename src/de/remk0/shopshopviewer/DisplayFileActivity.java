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

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxInputStream;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;

import de.remk0.shopshopviewer.ShopShopViewerApplication.AppState;

/**
 * @author Remko Plantenga
 * 
 */
public class DisplayFileActivity extends Activity {

    private ShopShopViewerApplication application;
    private DropboxAPI<AndroidAuthSession> mDBApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.application = (ShopShopViewerApplication) getApplicationContext();
        this.application.setAppState(AppState.DISPLAY);

        this.mDBApi = application.getDropboxAPI();

        setContentView(R.layout.main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Entry entry = this.application.getCurrentEntry();
        DropboxInputStream dbis = null;

        try {
            dbis = mDBApi.getFileStream(entry.path, null);
        } catch (DropboxServerException e) {
            switch (e.error) {
            default:
                Log.e(ShopShopViewerApplication.APP_NAME,
                        "Error retrieving file " + entry.fileName(), e);
                break;
            }
        } catch (DropboxException e) {
            Log.e(ShopShopViewerApplication.APP_NAME, "Error retrieving file "
                    + entry.fileName(), e);
        }

        if (dbis != null) {
            try {
                NSDictionary rootDict = (NSDictionary) PropertyListParser
                        .parse(dbis);
                NSObject[] colors = ((NSArray) rootDict.objectForKey("color"))
                        .getArray();

                for (NSObject c : colors) {
                    Log.d(ShopShopViewerApplication.APP_NAME, c.toString());
                }

                NSObject[] shoppingList = ((NSArray) rootDict
                        .objectForKey("shoppingList")).getArray();

                for (NSObject item : shoppingList) {
                    NSDictionary i = (NSDictionary) item;
                    NSObject done = i.objectForKey("done");
                    Log.d(ShopShopViewerApplication.APP_NAME, done.toString());

                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    dbis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else {
            Log.d(ShopShopViewerApplication.APP_NAME,
                    "Returned empty DropBoxInputStream-Object");
        }
    }
}
