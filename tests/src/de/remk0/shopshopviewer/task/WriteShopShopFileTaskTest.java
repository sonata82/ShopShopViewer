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
package de.remk0.shopshopviewer.task;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import de.remk0.shopshopviewer.io.FileAccess;
import de.remk0.shopshopviewer.parse.ShopShopFileParser;

/**
 * 
 * @author Remko Plantenga
 * 
 */
public class WriteShopShopFileTaskTest extends TestCase {

    public void testExecute() throws Exception {
        WriteShopShopFileTask task = new WriteShopShopFileTask();

        FileAccess fileAccess = EasyMock.createMock(FileAccess.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                out);
        EasyMock.expect(fileAccess.openFile("file1")).andReturn(
                bufferedOutputStream);
        EasyMock.replay(fileAccess);
        task.setFileAccess(fileAccess);

        ShopShopFileParser parser = EasyMock
                .createMock(ShopShopFileParser.class);
        EasyMock.expect(parser.write()).andReturn("123456".getBytes());
        EasyMock.replay(parser);
        task.setParser(parser);

        task.execute(new String[] { "file1" });

        assertTrue(task.get());

        assertEquals("123456", out.toString());

    }

}
