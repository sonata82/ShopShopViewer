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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;

import de.remk0.shopshopviewer.ShopShopViewerApplication.AppState;

/**
 * The main activity that shows a list of files and allows the user to
 * synchronize with Dropbox.
 * 
 * @author Remko Plantenga
 * 
 */
public class ShopShopViewerActivity extends ListActivity {
    private static final int DIALOG_DROPBOX_FAILED = 1;
    private static final int DIALOG_STORAGE_ERROR = 2;
    private static final int DIALOG_PROGRESS_SYNC = 3;
    private static final int MAX_FILES = 10000;
    private static final int REQUEST_CODE = 1;
    private static final String REVISIONS_STORE = "REV_STORE";
    private static final String SHOPSHOP_EXTENSION = ".shopshop";
    private static final String DROPBOX_FOLDER = "/ShopShop";

    private ShopShopViewerApplication application;
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private Map<String, ?> revisionsStore;
    private ArrayAdapter<String> listAdapter;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (ShopShopViewerApplication) getApplicationContext();
        application.setAppState(AppState.STARTED);

        if (!application.isExternalStorageAvailable()) {
            showDialog(DIALOG_STORAGE_ERROR);
        } else {
            getFiles();
        }
    }

    private void getFiles() {
        File appFolder = getExternalFilesDir(null);
        String[] files = appFolder.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(SHOPSHOP_EXTENSION)) {
                    return true;
                }
                return false;
            }
        });

        listAdapter = new ArrayAdapter<String>(this, R.layout.filelist, files);
        setListAdapter(listAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.listview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.synchronize:
            startDropboxSynchronize();
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void startDropboxSynchronize() {
        application.setAppState(AppState.SYNCHRONIZE);
        startActivityForResult(new Intent(this, DropboxAuthActivity.class),
                REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mDBApi = application.getDropboxAPI();
                retrieveRevisions();
            } else {
                showDialog(DIALOG_DROPBOX_FAILED);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mDBApi != null
                && application.getAppState() == AppState.AUTH_SUCCESS) {
            showDialog(DIALOG_PROGRESS_SYNC);
            new DropboxSynchronizeTask().execute();
        }

        application.setAppState(AppState.WAITING);
    }

    private class DropboxSynchronizeTask extends
            AsyncTask<Void, Integer, Boolean> {

        private String hash = null;

        @Override
        protected Boolean doInBackground(Void... params) {
            DropboxAPI.Entry dbe = null;
            String tempHash = this.hash;
            try {
                dbe = mDBApi.metadata(DROPBOX_FOLDER, MAX_FILES, tempHash,
                        true, null);
            } catch (DropboxServerException e) {
                switch (e.error) {
                case 304:
                    if (tempHash != null) {
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
                            buf = new BufferedOutputStream(
                                    new FileOutputStream(new File(
                                            getExternalFilesDir(null),
                                            e.fileName())));

                            try {
                                mDBApi.getFile(e.path, null, buf, null);
                            } catch (DropboxException e1) {
                                Log.e(ShopShopViewerApplication.APP_NAME,
                                        "Error while downloading "
                                                + e.fileName(), e1);
                            }
                        } catch (FileNotFoundException e2) {
                            Log.e(ShopShopViewerApplication.APP_NAME,
                                    "Error while opening file " + e.fileName(),
                                    e2);
                        } finally {
                            try {
                                buf.close();

                                storeRevision(e.path, e.rev);

                                publishProgress(++i, dbe.contents.size());
                            } catch (IOException e1) {
                                Log.e(ShopShopViewerApplication.APP_NAME,
                                        "Error while closing file "
                                                + e.fileName(), e1);
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

        @Override
        protected void onProgressUpdate(Integer... values) {
            float count = values[0];
            float total = values[1];
            Log.d(ShopShopViewerApplication.APP_NAME, "Progress updated to "
                    + count + " " + total + " " + +(count / total * 100f));
            progressDialog.setProgress((int) (count / total * 100));
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dismissDialog(DIALOG_PROGRESS_SYNC);
            if (result) {
                getFiles();
            }
        }

    }

    private String getRevision(String filename) {
        return (String) revisionsStore.get(filename);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String e = (String) this.getListAdapter().getItem(position);
        this.application.setCurrentFile(e);
        startActivity(new Intent(this, DisplayFileActivity.class));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id) {
        case DIALOG_DROPBOX_FAILED:
            builder.setMessage(
                    "Connection to Dropbox failed, please check your internet connection.")
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {

                                }
                            });
            return builder.create();
        case DIALOG_STORAGE_ERROR:
            builder.setMessage("External storage is not available.")
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {

                                }
                            });
            return builder.create();
        case DIALOG_PROGRESS_SYNC:
            progressDialog = new ProgressDialog(ShopShopViewerActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("Synchronizing...");
            return progressDialog;
        default:
            return null;
        }
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
        case DIALOG_PROGRESS_SYNC:
            progressDialog.setProgress(0);
        }
    }

    private void storeRevision(String filename, String rev) {
        SharedPreferences prefs = getSharedPreferences(REVISIONS_STORE,
                MODE_PRIVATE);
        Editor edit = prefs.edit();
        edit.putString(filename, rev).commit();
    }

    private void retrieveRevisions() {
        SharedPreferences prefs = getSharedPreferences(REVISIONS_STORE,
                MODE_PRIVATE);
        this.revisionsStore = prefs.getAll();
    }

}