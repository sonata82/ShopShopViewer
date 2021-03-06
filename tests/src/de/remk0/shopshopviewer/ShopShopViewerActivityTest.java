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

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.remk0.shopshopviewer.ShopShopViewerApplication.AppState;

/**
 * @author Remko Plantenga
 * 
 */
public class ShopShopViewerActivityTest extends
        ActivityInstrumentationTestCase2<ShopShopViewerActivity> {

    private Solo solo;

    public ShopShopViewerActivityTest() {
        super("de.remk0.shopshopviewer", ShopShopViewerActivity.class);
    }

    @Override
    protected void setUp() {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    protected void tearDown() {
        solo.finishOpenedActivities();
    }

    public void testStartSynchronizing() {
        String synchronize = getActivity().getResources().getString(
                R.string.synchronize);
        solo.clickOnMenuItem(synchronize);
        Activity currentActivity = solo.getCurrentActivity();
        assertNotNull(currentActivity);
        assertEquals("com.dropbox.client2.android.AuthActivity",
                currentActivity.getComponentName().getClassName());
        ShopShopViewerApplication applicationContext = (ShopShopViewerApplication) currentActivity
                .getApplicationContext();
        assertEquals(AppState.SWITCH_TO_DROPBOX,
                applicationContext.getAppState());
    }

}
