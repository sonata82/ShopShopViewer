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

import de.remk0.test.AndroidTestCaseWithResources;

/**
 * @author Remko Plantenga
 * 
 */
public class PlistParserTest extends AndroidTestCaseWithResources {

    public void testRead() throws Exception {
        PlistParser parser = new PlistParser();
        InputStream is = getResources("de.remk0.shopshopviewer.test")
                .openRawResource(de.remk0.shopshopviewer.test.R.raw.nederland2);
        assertTrue(parser.read(is));

        NSDictionary root = parser.getRoot();
        assertNotNull(root);

        NSObject[] shoppingList = parser.getShoppingList();
        assertNotNull(shoppingList);
        assertEquals(14, shoppingList.length);
        assertEquals("zahnpasta", ((NSDictionary) shoppingList[0])
                .objectForKey("name").toString());
        assertEquals(true,
                Boolean.parseBoolean((((NSDictionary) shoppingList[0])
                        .objectForKey("done")).toString()));
    }
}
