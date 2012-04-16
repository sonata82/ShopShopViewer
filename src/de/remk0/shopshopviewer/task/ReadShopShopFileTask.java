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
import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;

import de.remk0.shopshopviewer.ShopShopViewerApplication;
import de.remk0.shopshopviewer.io.FileAccess;
import de.remk0.shopshopviewer.parse.ShopShopFileParser;
import de.remk0.shopshopviewer.parse.ShopShopFileParserException;

/**
 * Task to read a ShopShop file.
 * 
 * @author Remko Plantenga
 * 
 */
public class ReadShopShopFileTask extends AsyncTask<String, Integer, Boolean> {

    private String fileName;
    private List<HashMap<String, Object>> rows;
    private FileAccess fileAccess;
    private ShopShopFileParser parser;

    public String getFileName() {
        return fileName;
    }

    public List<HashMap<String, Object>> getRows() {
        return rows;
    }

    public void setFileAccess(FileAccess fileAccess) {
        this.fileAccess = fileAccess;
    }

    public NSDictionary getRoot() {
        return parser.getRoot();
    }

    public NSObject[] getShoppingList() {
        return parser.getShoppingList();
    }

    @Override
    protected final Boolean doInBackground(String... params) {

        fileName = params[0];

        File f = fileAccess.getFile(fileName
                .concat(ShopShopViewerApplication.SHOPSHOP_EXTENSION));

        try {
            parser.read(f);
            return true;
        } catch (ShopShopFileParserException e) {
            Log.e(ShopShopViewerApplication.APP_NAME, e.toString());
        }
        return false;
    }

}