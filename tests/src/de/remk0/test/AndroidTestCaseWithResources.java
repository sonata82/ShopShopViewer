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
package de.remk0.test;

import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.test.AndroidTestCase;

/**
 * Extension of Androids base class for test cases. Adds functionality to load
 * resources from a known package.
 * 
 * @author Remko Plantenga
 * 
 */
public class AndroidTestCaseWithResources extends AndroidTestCase {

    protected Resources getResources(String packageName)
            throws NameNotFoundException {
        PackageManager pm = getContext().getPackageManager();
        return pm.getResourcesForApplication(packageName);
    }

}
