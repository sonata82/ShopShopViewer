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
package de.remk0.shopshopviewer;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import com.dd.plist.NSString;

/**
 * 
 * 
 * @author Remko Plantenga
 * 
 */
public class CheckableArrayAdapter<T> extends ArrayAdapter<T> {

    public CheckableArrayAdapter(Context context, int resource,
            int textViewResourceId, T[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    /**
     * Helper class to store a reference to the current visible view items and
     * the corresponding listview.
     * 
     * @author Remko Plantenga
     * 
     */
    static class ViewHolder {
        protected CheckBox checkbox;
        protected TextView name;
        protected TextView count;
        protected ListView listView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        if (convertView == null) {
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.checkbox = (CheckBox) v.findViewById(R.id.done);
            viewHolder.name = (TextView) v.findViewById(R.id.name);
            viewHolder.count = (TextView) v.findViewById(R.id.count);
            viewHolder.listView = (ListView) parent;
            viewHolder.checkbox.setTag(position);
            viewHolder.checkbox.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Integer pos = (Integer) v.getTag();
                    viewHolder.listView.performItemClick((View) v.getParent(),
                            pos, pos);
                }
            });
            v.setTag(viewHolder);
        } else {
            ((ViewHolder) v.getTag()).checkbox.setTag(position);
        }
        ViewHolder holder = (ViewHolder) v.getTag();
        NSDictionary item = (NSDictionary) this.getItem(position);
        holder.checkbox.setChecked(((NSNumber) item.objectForKey("done"))
                .boolValue());
        holder.name.setText(((NSString) item.objectForKey("name")).toString());
        holder.count
                .setText(((NSString) item.objectForKey("count")).toString());
        return v;
    }

}
