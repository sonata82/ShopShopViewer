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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI.Entry;

/**
 * Custom ArrayAdapter for {@link Entry entries} of a Dropbox folder.
 * 
 * @author Remko Plantenga
 * 
 */
public class ListOfDropBoxEntriesAdapter<T> extends ArrayAdapter<T> {

    private Context context;
    private int textViewResourceId;
    private List<T> objects;

    public ListOfDropBoxEntriesAdapter(Context context, int textViewResourceId,
            List<T> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.objects = objects;
    }

    static class ViewHolder {
        public TextView textView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        TextView textView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            textView = (TextView) inflater.inflate(this.textViewResourceId,
                    parent, false);
            holder = new ViewHolder();
            holder.textView = textView;
        } else {
            textView = (TextView) convertView;
        }
        Entry t = (Entry) objects.get(position);
        textView.setText(t.fileName().substring(0,
                t.fileName().indexOf(".shopshop")));

        return textView;
    }
}
