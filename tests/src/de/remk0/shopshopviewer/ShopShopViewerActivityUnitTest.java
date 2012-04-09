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

import org.easymock.EasyMock;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.view.MenuItem;

/**
 * @author Remko Plantenga
 * 
 */
public class ShopShopViewerActivityUnitTest extends
        ActivityUnitTestCase<ShopShopViewerActivity> {

    public ShopShopViewerActivityUnitTest() {
        super(ShopShopViewerActivity.class);
    }

    public void testStartSynchronize() {
        this.startActivity(new Intent(), null, null);
        MenuItem menuItem = EasyMock.createMock(MenuItem.class);
        EasyMock.expect(menuItem.getItemId()).andReturn(R.id.synchronize);
        EasyMock.replay(menuItem);
        this.getActivity().onOptionsItemSelected(menuItem);

        Intent startedActivityIntent = getStartedActivityIntent();
        assertNotNull(startedActivityIntent);
        assertEquals("de.remk0.shopshopviewer.DropboxAuthActivity",
                startedActivityIntent.getComponent().getClassName());
        EasyMock.verify(menuItem);
    }
}
