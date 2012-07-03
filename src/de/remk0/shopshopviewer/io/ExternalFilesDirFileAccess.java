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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;

/**
 * Provides file access to the external files dir of the device.
 * 
 * @author Remko Plantenga
 * 
 */
public class ExternalFilesDirFileAccess implements FileAccess {

    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private File externalFilesDir;

    public ExternalFilesDirFileAccess(Context context) {
        this.externalFilesDir = context.getExternalFilesDir(null);
    }

    @Override
    public BufferedOutputStream openFile(String filename)
            throws FileAccessException {
        try {
            return new BufferedOutputStream(new FileOutputStream(new File(
                    externalFilesDir, filename)), DEFAULT_BUFFER_SIZE);
        } catch (FileNotFoundException e) {
            throw new FileAccessException(e);
        }
    }

    @Override
    public InputStream getFile(String filename) throws FileAccessException {
        File f = new File(this.externalFilesDir, filename);
        try {
            return new FileInputStream(f);
        } catch (FileNotFoundException e) {
            throw new FileAccessException(e);
        }
    }

}
