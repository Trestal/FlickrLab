package com.lab.flickr.network;

import android.os.Parcel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class DataWrapperTest {

	private DataWrapper dataWrapper;

	@Before
	public void setup() {
		dataWrapper = new DataWrapper();
		dataWrapper.setValue(DataWrapper.Key.URL, "www.some.url");
		dataWrapper.setValue(DataWrapper.Key.INDEX, 1);
	}

	@Test
	public void testParcelling() {
		assertNotNull(dataWrapper);
		Parcel parcel = Parcel.obtain();
		dataWrapper.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);

		DataWrapper newWrapper = DataWrapper.CREATOR.createFromParcel(parcel);
		assertEquals(dataWrapper.getValue(DataWrapper.Key.URL), newWrapper.getValue(DataWrapper.Key.URL));
		assertEquals(dataWrapper.getValue(DataWrapper.Key.INDEX), newWrapper.getValue(DataWrapper.Key.INDEX));

		parcel.recycle();
	}
}
