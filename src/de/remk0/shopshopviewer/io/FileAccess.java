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
package de.remk0.shopshopviewer.io;

import java.io.BufferedOutputStream;
import java.io.InputStream;

/**
 * Interface for accessing files on the device.
 * 
 * @author Remko Plantenga
 * 
 */
public interface FileAccess {

    public abstract BufferedOutputStream openFile(String filename)
            throws FileAccessException;

    public abstract InputStream getFile(String filename)
            throws FileAccessException;

}
