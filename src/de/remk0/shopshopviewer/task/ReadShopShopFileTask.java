/**
 * ShopShopViewer
 * Copyright (C) 2012 Remko Plantenga
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
package de.remk0.shopshopviewer.task;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import com.dd.plist.NSObject;
import com.dd.plist.NSString;
import com.dd.plist.PropertyListParser;

import de.remk0.shopshopviewer.ShopShopViewerApplication;

/**
 * Task to read a ShopShop file.
 * 
 * @author Remko Plantenga
 * 
 */
public class ReadShopShopFileTask extends AsyncTask<Object, Integer, Boolean> {

    private String fileName;
    private List<HashMap<String, Object>> rows;

    public String getFileName() {
        return fileName;
    }

    public List<HashMap<String, Object>> getRows() {
        return rows;
    }

    @Override
    protected final Boolean doInBackground(Object... params) {

        fileName = (String) params[1];

        File f = new File((File) params[0],
                fileName.concat(ShopShopViewerApplication.SHOPSHOP_EXTENSION));

        try {
            NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(f);
            NSObject[] colors = ((NSArray) rootDict.objectForKey("color"))
                    .getArray();

            for (NSObject c : colors) {
                Log.d(ShopShopViewerApplication.APP_NAME, c.toString());
            }

            NSObject[] shoppingList = ((NSArray) rootDict
                    .objectForKey("shoppingList")).getArray();

            rows = new ArrayList<HashMap<String, Object>>();
            int j = 0, k = shoppingList.length;

            for (NSObject item : shoppingList) {
                NSDictionary i = (NSDictionary) item;
                NSString name = (NSString) i.objectForKey("name");
                NSNumber done = (NSNumber) i.objectForKey("done");
                NSString count = (NSString) i.objectForKey("count");

                HashMap<String, Object> row = new HashMap<String, Object>();
                row.put("name", name.toString());
                row.put("done", done.intValue());
                row.put("count", count.toString());

                rows.add(row);
                publishProgress(++j, k);
            }

            return true;
        } catch (Exception e) {
            Log.e(ShopShopViewerApplication.APP_NAME, e.toString());
        }
        return false;
    }

}