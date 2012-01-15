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

import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import de.remk0.shopshopviewer.ShopShopViewerApplication.AppState;
import de.remk0.shopshopviewer.task.ReadShopShopFileTask;

/**
 * Activity that displays a shopping list.
 * 
 * @author Remko Plantenga
 * 
 */
public class DisplayFileActivity extends ListActivity {

    private static final int DIALOG_READ_ERROR = 0;
    private static final int DIALOG_PROGRESS_READ = 1;
    private ShopShopViewerApplication application;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.application = (ShopShopViewerApplication) getApplicationContext();
        this.application.setAppState(AppState.DISPLAY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String fileName = this.application.getCurrentFile();
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
                showFile(getRows());
            } else {
                showDialog(DIALOG_READ_ERROR);
            }
        }
    }

    private void showFile(List<HashMap<String, Object>> rows) {
        CheckableSimpleAdapter adapter = new CheckableSimpleAdapter(
                DisplayFileActivity.this, rows, R.layout.file_row,
                new String[] { "done", "name", "count" }, new int[] {
                        R.id.done, R.id.name, R.id.count });
        adapter.setViewBinder(new ShopShopListBinder());
        setListAdapter(adapter);
    }

    class ShopShopListBinder implements ViewBinder {

        @Override
        public boolean setViewValue(View view, final Object data,
                String textRepresentation) {
            if (view instanceof CheckBox) {
                CheckBox cb = (CheckBox) view;
                if ("1".equals(textRepresentation)) {
                    cb.setChecked(true);
                } else {
                    cb.setChecked(false);
                }
                return true;
            }
            return false;
        }

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        HashMap<String, Object> map = (HashMap<String, Object>) this
                .getListView().getItemAtPosition(position);
        if ((Integer) map.get("done") == 0) {
            map.put("done", 1);
        } else {
            map.put("done", 0);
        }
        SimpleAdapter adapter = (SimpleAdapter) l.getAdapter();
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
            progressDialog = new ProgressDialog(DisplayFileActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("Reading...");
            return progressDialog;
        default:
            return null;
        }
    }
}
