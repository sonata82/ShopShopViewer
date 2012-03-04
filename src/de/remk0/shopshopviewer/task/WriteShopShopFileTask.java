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
import java.io.IOException;

import android.os.AsyncTask;
import android.util.Log;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;

import de.remk0.shopshopviewer.ShopShopViewerApplication;

/**
 * Task to write a ShopShop file.
 * 
 * @author Remko Plantenga
 * 
 */
public class WriteShopShopFileTask extends AsyncTask<Object, Integer, Boolean> {
    private String fileName;
    protected NSDictionary rootDict;

    @Override
    protected Boolean doInBackground(Object... params) {
        fileName = (String) params[1];

        File f = new File((File) params[0],
                fileName.concat(ShopShopViewerApplication.SHOPSHOP_EXTENSION));

        try {
            PropertyListParser.saveAsXML(rootDict, f);

            return true;
        } catch (IOException e) {
            Log.e(ShopShopViewerApplication.APP_NAME, e.toString());
        }
        return false;
    }

}
