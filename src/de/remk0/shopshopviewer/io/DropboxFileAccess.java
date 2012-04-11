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
package de.remk0.shopshopviewer.io;

import java.io.BufferedOutputStream;

import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;

import de.remk0.shopshopviewer.ShopShopViewerApplication;

/**
 * Provides remote file access to Dropbox.
 * 
 * @author Remko Plantenga
 * 
 */
public class DropboxFileAccess implements RemoteFileAccess {
    private static final String DROPBOX_FOLDER = "/ShopShop";
    private static final int MAX_FILES = 10000;

    private DropboxAPI<AndroidAuthSession> mDBApi;

    public DropboxFileAccess(DropboxAPI<AndroidAuthSession> mDBApi) {
        this.mDBApi = mDBApi;
    }

    @Override
    public Entry getEntriesForAppFolder(String hash)
            throws RemoteFileAccessException {
        try {
            return mDBApi.metadata(DROPBOX_FOLDER, MAX_FILES, hash, true, null);
        } catch (DropboxServerException e) {
            switch (e.error) {
            case 304:
                if (hash != null) {
                    Log.i(ShopShopViewerApplication.APP_NAME,
                            "Folder has not changed since last request");
                    return null;
                }
            default:
                throw new RemoteFileAccessException(e);
            }
        } catch (DropboxException e) {
            throw new RemoteFileAccessException(e);
        }
    }

    @Override
    public void getFile(String path, BufferedOutputStream out)
            throws RemoteFileAccessException {
        try {
            mDBApi.getFile(path, null, out, null);
        } catch (DropboxException e) {
            throw new RemoteFileAccessException(e);
        }

    }

}
