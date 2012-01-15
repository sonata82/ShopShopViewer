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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;

import de.remk0.shopshopviewer.ShopShopViewerApplication;

/**
 * Task that synchronizes with Dropbox.
 * 
 * @author Remko Plantenga
 * 
 */
public class DropboxSynchronizeTask extends AsyncTask<Void, Integer, Boolean> {
    private static final String DROPBOX_FOLDER = "/ShopShop";
    private static final int MAX_FILES = 10000;
    private String hash;
    private Map<String, ?> revisionsStore;
    private File externalFilesDir;
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private Editor revisionsEditor;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setRevisionsStore(Map<String, ?> revisionsStore) {
        this.revisionsStore = revisionsStore;
    }

    public void setExternalFilesDir(File externalFilesDir) {
        this.externalFilesDir = externalFilesDir;
    }

    public void setmDBApi(DropboxAPI<AndroidAuthSession> mDBApi) {
        this.mDBApi = mDBApi;
    }

    public void setRevisionsEditor(Editor revisionsEditor) {
        this.revisionsEditor = revisionsEditor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Boolean doInBackground(Void... params) {
        DropboxAPI.Entry dbe = null;
        try {
            dbe = mDBApi.metadata(DROPBOX_FOLDER, MAX_FILES, hash, true, null);
        } catch (DropboxServerException e) {
            switch (e.error) {
            case 304:
                if (hash != null) {
                    Log.i(ShopShopViewerApplication.APP_NAME,
                            "Folder has not changed since last request");
                    break;
                }
            default:
                Log.e(ShopShopViewerApplication.APP_NAME,
                        "Error retrieving folder", e);
                break;
            }
        } catch (DropboxException e) {
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
                        buf = new BufferedOutputStream(new FileOutputStream(
                                new File(externalFilesDir, e.fileName())));

                        try {
                            mDBApi.getFile(e.path, null, buf, null);
                        } catch (DropboxException e1) {
                            Log.e(ShopShopViewerApplication.APP_NAME,
                                    "Error while downloading " + e.fileName(),
                                    e1);
                        }
                    } catch (FileNotFoundException e2) {
                        Log.e(ShopShopViewerApplication.APP_NAME,
                                "Error while opening file " + e.fileName(), e2);
                    } finally {
                        try {
                            buf.close();

                            setRevision(e.path, e.rev);

                            publishProgress(++i, dbe.contents.size());
                        } catch (IOException e1) {
                            Log.e(ShopShopViewerApplication.APP_NAME,
                                    "Error while closing file " + e.fileName(),
                                    e1);
                        }
                    }
                }
            }
            return true;
        } else {
            Log.d(ShopShopViewerApplication.APP_NAME,
                    "Returned empty DropBoxEntry-Object");
        }

        return false;

    }

    private String getRevision(String filename) {
        return (String) revisionsStore.get(filename);
    }

    private void setRevision(String filename, String revision) {
        revisionsEditor.putString(filename, revision).commit();
    }
}
