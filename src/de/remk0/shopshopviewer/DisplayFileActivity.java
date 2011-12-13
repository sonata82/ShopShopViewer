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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;

import de.remk0.shopshopviewer.ShopShopViewerApplication.AppState;

/**
 * Activity that displays a shopping list.
 * 
 * @author Remko Plantenga
 * 
 */
public class DisplayFileActivity extends Activity {

    private ShopShopViewerApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.application = (ShopShopViewerApplication) getApplicationContext();
        this.application.setAppState(AppState.DISPLAY);

        setContentView(R.layout.main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String fileName = this.application.getCurrentFile();
        File f = new File(getExternalFilesDir(null), fileName);

        try {
            NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(f);
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
        }

    }
}
