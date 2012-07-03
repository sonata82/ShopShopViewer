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

import java.io.InputStream;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;

/**
 * Interface for parsing ShopShop files.
 * 
 * @author Remko Plantenga
 * 
 */
public interface ShopShopFileParser {

    public abstract boolean read(InputStream is)
            throws ShopShopFileParserException;

    public abstract NSDictionary getRoot();

    public abstract NSObject[] getShoppingList();

    public abstract byte[] write() throws ShopShopFileParserException;
}
