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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;

import de.remk0.test.AndroidTestCaseWithResources;

/**
 * @author Remko Plantenga
 * 
 */
public class PlistParserTest extends AndroidTestCaseWithResources {

    private PlistParser parser;

    @Override
    protected void setUp() throws Exception {
        parser = new PlistParser();
        InputStream is = getResources("de.remk0.shopshopviewer.test")
                .openRawResource(de.remk0.shopshopviewer.test.R.raw.nederland2);
        assertTrue(parser.read(is));
    }

    public void testRead() {
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

    public void testWrite() throws Exception {

        byte[] contentAsBytes = parser.write();
        ByteArrayInputStream written = new ByteArrayInputStream(contentAsBytes);

        assertNotNull(written);

        NSDictionary root = (NSDictionary) PropertyListParser.parse(written);

        assertEquals(2, root.count());

        NSObject[] shoppingList = ((NSArray) root.objectForKey("shoppingList"))
                .getArray();

        assertEquals(14, shoppingList.length);
    }
}
