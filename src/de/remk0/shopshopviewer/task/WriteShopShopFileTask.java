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

import java.io.BufferedOutputStream;
import java.io.IOException;

import android.os.AsyncTask;
import android.util.Log;
import de.remk0.shopshopviewer.ShopShopViewerApplication;
import de.remk0.shopshopviewer.io.FileAccess;
import de.remk0.shopshopviewer.io.FileAccessException;
import de.remk0.shopshopviewer.parse.ShopShopFileParser;
import de.remk0.shopshopviewer.parse.ShopShopFileParserException;

/**
 * Task to write a ShopShop file.
 * 
 * @author Remko Plantenga
 * 
 */
public class WriteShopShopFileTask extends AsyncTask<String, Integer, Boolean> {
    private String fileName;
    private FileAccess fileAccess;
    private ShopShopFileParser parser;

    public void setFileAccess(FileAccess fileAccess) {
        this.fileAccess = fileAccess;
    }

    public void setParser(ShopShopFileParser parser) {
        this.parser = parser;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        fileName = params[0];

        BufferedOutputStream out;
        try {
            out = fileAccess.openFile(fileName);
        } catch (FileAccessException e) {
            Log.e(ShopShopViewerApplication.APP_NAME, e.toString());
            return false;
        }

        try {

            out.write(parser.write());

            return true;
        } catch (ShopShopFileParserException e) {
            Log.e(ShopShopViewerApplication.APP_NAME, e.toString());
        } catch (IOException e) {
            Log.e(ShopShopViewerApplication.APP_NAME, e.toString());
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                Log.e(ShopShopViewerApplication.APP_NAME, e.toString());
            }
        }
        return false;
    }
}
