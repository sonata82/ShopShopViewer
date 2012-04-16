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
package de.remk0.shopshopviewer.parse;

import java.io.File;

import android.util.Log;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;

import de.remk0.shopshopviewer.ShopShopViewerApplication;

/**
 * Parses ShopShop files using plist (http://code.google.com/p/plist/).
 * 
 * @author Remko Plantenga
 * 
 */
public class PlistParser implements ShopShopFileParser {

    private NSDictionary root;
    private NSObject[] shoppingList;

    @Override
    public boolean read(File f) throws ShopShopFileParserException {
        try {
            root = (NSDictionary) PropertyListParser.parse(f);

            NSObject[] colors = ((NSArray) root.objectForKey("color"))
                    .getArray();

            for (NSObject c : colors) {
                Log.d(ShopShopViewerApplication.APP_NAME, c.toString());
            }

            shoppingList = ((NSArray) root.objectForKey("shoppingList"))
                    .getArray();

            return true;
        } catch (Exception e) {
            throw new ShopShopFileParserException(e);
        }
    }

    @Override
    public NSDictionary getRoot() {
        return root;
    }

    @Override
    public NSObject[] getShoppingList() {
        return shoppingList;
    }
}
