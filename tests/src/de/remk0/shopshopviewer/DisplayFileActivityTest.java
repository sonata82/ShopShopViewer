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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.easymock.EasyMock;
import org.easymock.IAnswer;

import android.app.ListActivity;
import android.content.Intent;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import com.dd.plist.NSObject;
import com.dd.plist.NSString;
import com.jayway.android.robotium.solo.Solo;

import de.remk0.shopshopviewer.io.FileAccess;
import de.remk0.shopshopviewer.io.FileAccessException;
import de.remk0.shopshopviewer.parse.ShopShopFileParser;
import de.remk0.test.ActivityInstrumentationTestCase2WithResources;

/**
 * @author Remko Plantenga
 * 
 */
public class DisplayFileActivityTest extends
        ActivityInstrumentationTestCase2WithResources<DisplayFileActivity> {
    Solo solo;

    public DisplayFileActivityTest() {
        super("de.remk0.shopshopviewer", DisplayFileActivity.class);
    }

    @Override
    protected void tearDown() {
        if (solo != null) {
            solo.finishOpenedActivities();
        }
    }

    public void testResume() throws Exception {
        ShopShopViewerApplication context = (ShopShopViewerApplication) this
                .getInstrumentation().getTargetContext()
                .getApplicationContext();
        final InputStream is = getResources("de.remk0.shopshopviewer.test")
                .openRawResource(de.remk0.shopshopviewer.test.R.raw.nederland2);
        prepareContext(context, is);

        Intent intent = new Intent();
        intent.putExtra("de.remk0.shopshopviewer.fileName", "file1");
        setActivityIntent(intent);

        solo = new Solo(getInstrumentation(), getActivity());

        solo.waitForView(ListView.class);

        ListAdapter currentListAdapter = ((ListActivity) solo
                .getCurrentActivity()).getListAdapter();
        assertNotNull(currentListAdapter);

        getInstrumentation().callActivityOnPause(getActivity());

        getInstrumentation().callActivityOnResume(getActivity());

        solo.waitForView(ListView.class);
    }

    public void testRotate() throws Exception {
    	ShopShopViewerApplication context = (ShopShopViewerApplication) this
                .getInstrumentation().getTargetContext()
                .getApplicationContext();
    	prepareContext(context, null);

        ShopShopFileParser parser = EasyMock
                .createMock(ShopShopFileParser.class);
        EasyMock.expect(parser.read(EasyMock.anyObject(InputStream.class)))
                .andReturn(true).times(2);
        NSObject[] shoppingList = new NSObject[3];
        NSDictionary item1 = new NSDictionary();
        item1.put("name", new NSString("item1"));
        item1.put("done", new NSNumber(0));
        item1.put("count", new NSString("3"));
        shoppingList[0] = item1;
        NSDictionary item2 = new NSDictionary();
        item2.put("name", new NSString("item2"));
        item2.put("done", new NSNumber(0));
        item2.put("count", new NSString("2"));
        shoppingList[1] = item2;
        NSDictionary item3 = new NSDictionary();
        item3.put("name", new NSString("item3"));
        item3.put("done", new NSNumber(0));
        item3.put("count", new NSString("1"));
        shoppingList[2] = item3;

        EasyMock.expect(parser.getShoppingList()).andReturn(shoppingList)
                .times(2);
        byte[] bytes = new byte[8];
        EasyMock.expect(parser.write()).andReturn(bytes).times(2);
        EasyMock.replay(parser);
        context.setShopShopFileParser(parser);

        Intent intent = new Intent();
        intent.putExtra("de.remk0.shopshopviewer.fileName", "file1");
        setActivityIntent(intent);

        solo = new Solo(getInstrumentation(), getActivity());

        solo.waitForView(ListView.class);

        solo.setActivityOrientation(Solo.LANDSCAPE);

        solo.waitForView(ListView.class);
    }

	private void prepareContext(ShopShopViewerApplication context, InputStream is)
			throws FileAccessException {
		
        FileAccess fileAccess = EasyMock.createMock(FileAccess.class);
        EasyMock.expect(fileAccess.getFile("file1")).andReturn(is).times(2);
        EasyMock.expect(fileAccess.openFile("file1"))
                .andAnswer(new IAnswer<BufferedOutputStream>() {
                    @Override
                    public BufferedOutputStream answer() throws Throwable {

                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        BufferedOutputStream bufferedOut = new BufferedOutputStream(
                                out);
                        return bufferedOut;
                    }
                }).times(2);
        EasyMock.replay(fileAccess);
        context.setFileAccess(fileAccess);
	}

}
