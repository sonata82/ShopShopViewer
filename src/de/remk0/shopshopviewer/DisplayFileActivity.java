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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import com.dd.plist.NSObject;

import de.remk0.shopshopviewer.ShopShopViewerApplication.AppState;
import de.remk0.shopshopviewer.task.ReadShopShopFileTask;
import de.remk0.shopshopviewer.task.WriteShopShopFileTask;

/**
 * Activity that displays a shopping list.
 * 
 * @author Remko Plantenga
 * 
 */
public class DisplayFileActivity extends ListActivity {

    private static final int DIALOG_READ_ERROR = 0;
    private static final int DIALOG_PROGRESS_READ = 1;
    private static final int DIALOG_PROGRESS_WRITE = 2;
    private ShopShopViewerApplication application;
    private ProgressDialog progressDialog;
    private String fileName;
    private NSDictionary root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.application = (ShopShopViewerApplication) getApplicationContext();
        this.application.setAppState(AppState.DISPLAY);

        fileName = getIntent().getExtras().getString(
                this.getPackageName() + ".fileName");
        this.setTitle(fileName);

        showDialog(DIALOG_PROGRESS_READ);
        new MyReadShopShopFile().execute(new Object[] {
                getExternalFilesDir(null), fileName });
    }

    class MyReadShopShopFile extends ReadShopShopFileTask {
        @Override
        protected void onProgressUpdate(Integer... values) {
            float count = values[0];
            float total = values[1];
            progressDialog.setProgress((int) (count / total * 100));
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                dismissDialog(DIALOG_PROGRESS_READ);
                root = rootDict;
                showFile(shoppingList);
            } else {
                showDialog(DIALOG_READ_ERROR);
            }
        }
    }

    private void showFile(NSObject[] list) {
        CheckableArrayAdapter<NSObject> adapter = new CheckableArrayAdapter<NSObject>(
                DisplayFileActivity.this, R.layout.file_row, R.id.name, list);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        NSDictionary item = (NSDictionary) this.getListView()
                .getItemAtPosition(position);
        if (((NSNumber) item.objectForKey("done")).boolValue()) {
            item.put("done", 0);
        } else {
            item.put("done", 1);
        }
        BaseAdapter adapter = (BaseAdapter) l.getAdapter();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_READ_ERROR:
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Error while reading")
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    finish();
                                }
                            });
            return builder.create();
        case DIALOG_PROGRESS_READ:
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Reading...");
            return progressDialog;
        case DIALOG_PROGRESS_WRITE:
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Writing...");
            return progressDialog;
        default:
            return null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        showDialog(DIALOG_PROGRESS_WRITE);
        new MyWriteShopShopFileTask().execute(new Object[] {
                getExternalFilesDir(null), fileName });
    }

    class MyWriteShopShopFileTask extends WriteShopShopFileTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            rootDict = root;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO: why does this line cause IllegalArgumentExceptions?
            // dismissDialog(DIALOG_PROGRESS_WRITE);

            super.onPostExecute(result);
        }
    }
}
