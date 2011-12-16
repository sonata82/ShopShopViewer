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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import com.dd.plist.NSObject;
import com.dd.plist.NSString;
import com.dd.plist.PropertyListParser;

import de.remk0.shopshopviewer.ShopShopViewerApplication.AppState;

/**
 * Activity that displays a shopping list.
 * 
 * @author Remko Plantenga
 * 
 */
public class DisplayFileActivity extends ListActivity {

    private ShopShopViewerApplication application;
    private List<HashMap<String, String>> rows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.application = (ShopShopViewerApplication) getApplicationContext();
        this.application.setAppState(AppState.DISPLAY);
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

            rows = new ArrayList<HashMap<String, String>>();

            for (NSObject item : shoppingList) {
                NSDictionary i = (NSDictionary) item;
                NSString name = (NSString) i.objectForKey("name");
                NSNumber done = (NSNumber) i.objectForKey("done");
                NSString count = (NSString) i.objectForKey("count");

                HashMap<String, String> row = new HashMap<String, String>();
                row.put("name", name.toString());
                row.put("done", done.toString());
                row.put("count", count.toString());

                rows.add(row);
            }

            ListAdapter adapter = new SimpleAdapter(this, rows,
                    R.layout.file_row,
                    new String[] { "name", "count", "done" }, new int[] {
                            R.id.name, R.id.count, R.id.done });
            this.setListAdapter(adapter);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
