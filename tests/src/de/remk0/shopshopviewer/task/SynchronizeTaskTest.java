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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IAnswer;

import android.content.SharedPreferences.Editor;

import com.dropbox.client2.DropboxAPI.Entry;

import de.remk0.shopshopviewer.io.FileAccess;
import de.remk0.shopshopviewer.io.RemoteFileAccess;

/**
 * 
 * @author Remko Plantenga
 * 
 */
public class SynchronizeTaskTest extends TestCase {

    public void testExecute() throws Exception {
        SynchronizeTask task = new SynchronizeTask();
        FileAccess fileAccess = EasyMock.createMock(FileAccess.class);
        OutputStream out = new ByteArrayOutputStream();
        BufferedOutputStream buffer = new BufferedOutputStream(out);
        EasyMock.expect(fileAccess.openFile("file1")).andReturn(buffer);
        EasyMock.replay(fileAccess);
        task.setFileAccess(fileAccess);
        task.setHash(null);
        RemoteFileAccess remoteFileAccess = EasyMock
                .createMock(RemoteFileAccess.class);
        Entry entry = createFolder();

        EasyMock.expect(remoteFileAccess.getEntriesForAppFolder(null))
                .andReturn(entry);
        remoteFileAccess.getFile(EasyMock.eq("/folder/file1"),
                EasyMock.isA(BufferedOutputStream.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {

            @Override
            public Object answer() throws Throwable {
                BufferedOutputStream buf = ((BufferedOutputStream) EasyMock
                        .getCurrentArguments()[1]);
                buf.write("Test".getBytes("UTF-8"));
                return null;
            }
        });
        EasyMock.replay(remoteFileAccess);
        task.setRemoteFileAccess(remoteFileAccess);
        Editor revisionsEditor = EasyMock.createMock(Editor.class);
        EasyMock.expect(revisionsEditor.putString("/folder/file1", "file1-1"))
                .andReturn(revisionsEditor);
        EasyMock.expect(revisionsEditor.commit()).andReturn(true);
        EasyMock.replay(revisionsEditor);
        task.setRevisionsEditor(revisionsEditor);
        Map<String, Object> revisionsStore = new HashMap<String, Object>();
        revisionsStore.put("/folder/file1", "file1-0");
        task.setRevisionsStore(revisionsStore);

        task.execute();

        assertTrue(task.get());

        assertEquals("Test", out.toString());
    }

    private Entry createFolder() {
        Entry root = createEntry();
        root.path = "/folder";
        root.hash = "newer";
        root.rev = "folder1";
        root.contents = new ArrayList<Entry>();
        root.contents.add(createEntry());
        return root;
    }

    private Entry createEntry() {
        Entry entry = new Entry();
        entry.path = "/folder/file1";
        entry.rev = "file1-1";
        return entry;
    }

}
