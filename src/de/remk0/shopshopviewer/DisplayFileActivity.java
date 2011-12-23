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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import com.dd.plist.NSObject;
import com.dd.plist.NSString;
import com.dd.plist.PropertyListParser;

import de.remk0.shopshopviewer.ShopShopViewerApplication.AppState;

/**
 * Activity that displays a shopping list.
 * 
 * @author Remko Plantenga
 * 
 */
public class DisplayFileActivity extends ListActivity {

    private static final int DIALOG_READ_ERROR = 0;
    private ShopShopViewerApplication application;
    private List<HashMap<String, Object>> rows;

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
        File f = new File(getExternalFilesDir(null),
                fileName.concat(ShopShopViewerApplication.SHOPSHOP_EXTENSION));

        try {
            NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(f);
            NSObject[] colors = ((NSArray) rootDict.objectForKey("color"))
                    .getArray();

            for (NSObject c : colors) {
                Log.d(ShopShopViewerApplication.APP_NAME, c.toString());
            }

            NSObject[] shoppingList = ((NSArray) rootDict
                    .objectForKey("shoppingList")).getArray();

            rows = new ArrayList<HashMap<String, Object>>();

            for (NSObject item : shoppingList) {
                NSDictionary i = (NSDictionary) item;
                NSString name = (NSString) i.objectForKey("name");
                NSNumber done = (NSNumber) i.objectForKey("done");
                NSString count = (NSString) i.objectForKey("count");

                HashMap<String, Object> row = new HashMap<String, Object>();
                row.put("name", name.toString());
                row.put("done", done.intValue());
                row.put("count", count.toString());

                rows.add(row);
            }

            CheckableSimpleAdapter adapter = new CheckableSimpleAdapter(this,
                    rows, R.layout.file_row, new String[] { "done", "name",
                            "count" }, new int[] { R.id.done, R.id.name,
                            R.id.count });
            adapter.setViewBinder(new ShopShopListBinder());
            this.setListAdapter(adapter);

        } catch (Exception e) {
            showDialog(DIALOG_READ_ERROR);
        }

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

        HashMap<String, Object> map = (HashMap) this.getListView()
                .getItemAtPosition(position);
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
        default:
            return null;
        }
    }
}
