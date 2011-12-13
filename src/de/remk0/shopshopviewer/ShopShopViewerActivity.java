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
import java.io.IOException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
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
    private static final int MAX_FILES = 10000;
    private static final int REQUEST_CODE = 1;

    private ShopShopViewerApplication application;
    private String hash = null;
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private String[] files;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (ShopShopViewerApplication) getApplicationContext();
        application.setAppState(AppState.STARTED);

        if (!application.isExternalStorageAvailable()) {
            showDialog(DIALOG_STORAGE_ERROR);
        } else {
            files = application.getFiles();

            setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item,
                    files));
        }
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
            dropboxSynchronize();
        }

        application.setAppState(AppState.WAITING);
    }

    private void dropboxSynchronize() {
        DropboxAPI.Entry dbe = null;
        String tempHash = this.hash;
        try {
            dbe = mDBApi.metadata(ShopShopViewerApplication.DROPBOX_FOLDER, MAX_FILES,
                    tempHash, true, null);
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
                        "Error retrieving folders", e);
                break;
            }
        } catch (DropboxException e) {
            Log.e(ShopShopViewerApplication.APP_NAME,
                    "Error retrieving folder", e);
        }

        if (dbe != null) {
            this.hash = dbe.hash;
            for (Entry e : dbe.contents) {
                BufferedOutputStream buf = null;
                try {
                    buf = new BufferedOutputStream(new FileOutputStream(
                            new File(getExternalFilesDir(null), e.fileName())));

                    try {
                        mDBApi.getFile(e.path, null, buf, null);
                    } catch (DropboxException e1) {
                        Log.e(ShopShopViewerApplication.APP_NAME,
                                "Error while downloading " + e.fileName(), e1);
                    }
                } catch (FileNotFoundException e2) {
                    Log.e(ShopShopViewerApplication.APP_NAME,
                            "Error while opening file " + e.fileName(), e2);
                } finally {
                    try {
                        buf.close();
                    } catch (IOException e1) {
                        Log.e(ShopShopViewerApplication.APP_NAME,
                                "Error while closing file " + e.fileName(), e1);
                    }
                }
            }
        } else {
            Log.d(ShopShopViewerApplication.APP_NAME,
                    "Returned empty DropBoxEntry-Object");
        }
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
        default:
            return null;
        }
    }

}