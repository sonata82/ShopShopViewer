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

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * Custom {@link SimpleAdapter} for displaying {@link LinearLayout} with a
 * checkbox. Clicking the checkbox performs the ListItemClick event on the
 * {@link ListView}.
 * 
 * @author Remko Plantenga
 * 
 */
public class CheckableSimpleAdapter extends SimpleAdapter {

    public CheckableSimpleAdapter(Context context,
            List<? extends Map<String, ?>> objects, int resource,
            String[] from, int[] to) {
        super(context, objects, resource, from, to);
    }

    static class ViewHolder {
        protected CheckBox checkbox;
        protected ListView listView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        if (convertView == null) {
            LinearLayout l = (LinearLayout) v;
            for (int i = 0, k = l.getChildCount(); i < k; i++) {
                View child = l.getChildAt(i);
                if (child instanceof CheckBox) {
                    final ViewHolder viewHolder = new ViewHolder();
                    viewHolder.checkbox = (CheckBox) child;
                    viewHolder.listView = (ListView) parent;
                    viewHolder.checkbox.setTag(position);
                    viewHolder.checkbox
                            .setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Integer pos = (Integer) v.getTag();
                                    viewHolder.listView.performItemClick(
                                            (View) v.getParent(), pos, pos);
                                }
                            });
                    v.setTag(viewHolder);
                    break;
                }
            }
        } else {
            ((ViewHolder) v.getTag()).checkbox.setTag(position);
        }
        return v;
    }
}