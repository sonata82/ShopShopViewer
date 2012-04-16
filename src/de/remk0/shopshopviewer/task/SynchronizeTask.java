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
import java.util.Map;

import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;

import de.remk0.shopshopviewer.ShopShopViewerApplication;
import de.remk0.shopshopviewer.io.FileAccess;
import de.remk0.shopshopviewer.io.FileAccessException;
import de.remk0.shopshopviewer.io.RemoteFileAccess;
import de.remk0.shopshopviewer.io.RemoteFileAccessException;

/**
 * Task that synchronizes a remote location with files on the device.
 * 
 * @author Remko Plantenga
 * 
 */
public class SynchronizeTask extends AsyncTask<Void, Integer, Boolean> {

    private String hash;
    private Map<String, ?> revisionsStore;
    private Editor revisionsEditor;
    private FileAccess fileAccess;
    private RemoteFileAccess remoteFileAccess;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setRevisionsStore(Map<String, ?> revisionsStore) {
        this.revisionsStore = revisionsStore;
    }

    public void setRevisionsEditor(Editor revisionsEditor) {
        this.revisionsEditor = revisionsEditor;
    }

    public void setFileAccess(FileAccess fileAccess) {
        this.fileAccess = fileAccess;
    }

    public void setRemoteFileAccess(RemoteFileAccess remoteFileAccess) {
        this.remoteFileAccess = remoteFileAccess;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        DropboxAPI.Entry dbe = null;
        try {
            dbe = remoteFileAccess.getEntriesForAppFolder(hash);
        } catch (RemoteFileAccessException e) {
            Log.e(ShopShopViewerApplication.APP_NAME,
                    "Error retrieving folder", e);
        }

        if (dbe != null) {
            this.hash = dbe.hash;
            int i = 0;
            for (Entry e : dbe.contents) {
                if (getRevision(e.path) != e.rev) {
                    BufferedOutputStream buf = null;
                    try {
                        buf = fileAccess.openFile(e.fileName());

                        try {
                            remoteFileAccess.getFile(e.path, buf);
                        } catch (RemoteFileAccessException e1) {
                            Log.e(ShopShopViewerApplication.APP_NAME,
                                    "Error while downloading " + e.fileName(),
                                    e1);
                        }
                    } catch (FileAccessException e2) {
                        Log.e(ShopShopViewerApplication.APP_NAME,
                                "Error while opening file " + e.fileName(), e2);
                    } finally {
                        if (buf != null) {
                            try {
                                buf.close();

                                setRevision(e.path, e.rev);

                            } catch (IOException e1) {
                                Log.e(ShopShopViewerApplication.APP_NAME,
                                        "Error while closing file "
                                                + e.fileName(), e1);
                            }
                        }
                        publishProgress(++i, dbe.contents.size());
                    }
                }
            }
            return true;
        }

        Log.d(ShopShopViewerApplication.APP_NAME,
                "Returned empty DropBoxEntry-Object");
        return false;

    }

    private String getRevision(String filename) {
        return (String) revisionsStore.get(filename);
    }

    private void setRevision(String filename, String revision) {
        revisionsEditor.putString(filename, revision).commit();
    }

}
