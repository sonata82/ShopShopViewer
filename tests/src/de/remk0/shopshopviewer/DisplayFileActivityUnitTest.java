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

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.test.ActivityUnitTestCase;

/**
 * @author Remko Plantenga
 * 
 */
public class DisplayFileActivityUnitTest extends
        ActivityUnitTestCase<DisplayFileActivity> {

    public DisplayFileActivityUnitTest() {
        super(DisplayFileActivity.class);
    }

    public void testResume() {
        Context mockedContext = new ContextWrapper(getInstrumentation()
                .getTargetContext());
        setActivityContext(mockedContext);

        Intent intent = new Intent();
        intent.putExtra("de.remk0.shopshopviewer.fileName", "test.xml");

        /*
         * uncomment when
         * http://code.google.com/p/android/issues/detail?id=14616 has been
         * fixed
         */
        // this.startActivity(intent, null, null);
    }

}
