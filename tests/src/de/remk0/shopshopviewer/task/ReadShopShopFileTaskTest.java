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

import java.io.InputStream;

import org.easymock.EasyMock;

import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.test.AndroidTestCase;
import de.remk0.shopshopviewer.io.FileAccess;
import de.remk0.shopshopviewer.parse.ShopShopFileParser;

/**
 * @author Remko Plantenga
 * 
 */
public class ReadShopShopFileTaskTest extends AndroidTestCase {

    public void testExecute() throws Exception {
        ReadShopShopFileTask task = new ReadShopShopFileTask();

        FileAccess fileAccess = EasyMock.createMock(FileAccess.class);
        final InputStream is = getResources("de.remk0.shopshopviewer.test")
                .openRawResource(de.remk0.shopshopviewer.test.R.raw.nederland2);
        EasyMock.expect(fileAccess.getFile("file1.shopshop")).andReturn(is);
        EasyMock.replay(fileAccess);
        task.setFileAccess(fileAccess);
        ShopShopFileParser parser = EasyMock
                .createMock(ShopShopFileParser.class);
        EasyMock.expect(parser.read(is)).andReturn(true);
        EasyMock.replay(parser);
        task.setParser(parser);

        task.execute(new String[] { "file1" });

        assertTrue(task.get());
    }

    protected Resources getResources(String packageName)
            throws NameNotFoundException {
        PackageManager pm = getContext().getPackageManager();
        return pm.getResourcesForApplication(packageName);
    }

}
