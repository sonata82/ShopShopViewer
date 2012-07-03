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
import java.io.InputStreamReader;
import java.io.Reader;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;

import de.remk0.test.AndroidTestCaseWithResources;

/**
 * @author Remko Plantenga
 * 
 */
public class PlistParserTest extends AndroidTestCaseWithResources {

    private PlistParser parser;
    private InputStream is;

    @Override
    protected void setUp() throws Exception {
        parser = new PlistParser();
        is = getResources("de.remk0.shopshopviewer.test").openRawResource(
                de.remk0.shopshopviewer.test.R.raw.nederland2);
    }

    public void testRead() throws Exception {
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

    public void testWrite() throws Exception {
        assertTrue(parser.read(is));

        // TODO reopen inputstream
        is = getResources("de.remk0.shopshopviewer.test").openRawResource(
                de.remk0.shopshopviewer.test.R.raw.nederland2);

        byte[] contentAsBytes = parser.write();
        ByteArrayInputStream written = new ByteArrayInputStream(contentAsBytes);
        String contantAsString = new String(contentAsBytes);

        //
        final char[] buffer = new char[0x10000];
        StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(is, "UTF-8");
        try {
            int read;
            do {
                read = in.read(buffer, 0, buffer.length);
                if (read > 0) {
                    out.append(buffer, 0, read);
                }
            } while (read >= 0);
        } finally {
            in.close();
        }
        String expectedAsString = out.toString();
        //

        assertNotNull(written);

        int expected;
        int actual;
        while (((expected = is.read()) != -1)
                && ((actual = written.read()) != -1)) {
            assertEquals(expected, actual);
        }

    }
}
