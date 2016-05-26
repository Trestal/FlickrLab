package com.lab.flickr.activities;

import android.os.Bundle;

import com.lab.flickr.network.DataWrapper;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;
import static junit.framework.Assert.*;


public class ActMainTest {

	private ActMain mockActMain;

	private BlockingQueue<DataWrapper> queue;

	@Before
	public void setup() throws NoSuchFieldException, IllegalAccessException {

		mockActMain = mock(ActMain.class);
//		urlLoadQueue = mock(Bundle.class);

		queue = new LinkedBlockingQueue<>();
		Field queueField = ActMain.class.getDeclaredField("queue");
		queueField.setAccessible(true);
		queueField.set(mockActMain, queue);
//		loadQueueField = ActMain.class.getDeclaredField("urlLoadQueue");
//		loadQueueField.setAccessible(true);
//
//		loadQueueField.set(mockActMain, urlLoadQueue);
	}

	@Test
	public void testPopulateLoadQueue() throws IOException, JSONException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, InterruptedException {
		JSONObject jsonObject = new JSONObject(readJsonFromFile());

		Method populateLoadQueue = ActMain.class.getDeclaredMethod("populateLoadQueue", JSONObject.class);
		populateLoadQueue.setAccessible(true);
		populateLoadQueue.invoke(mockActMain, jsonObject);

		int numItems = getNumberOfItems(jsonObject);

		assertEquals(queue.size(), numItems + 1); //+1 for poison pill
		assertEquals(queue.take().getValue(DataWrapper.Key.URL), "http://farm2.staticflickr.com/1524/24897666730_1af4b3e837_c.jpg");
		assertEquals(queue.take().getValue(DataWrapper.Key.URL), "http://farm2.staticflickr.com/1624/25193301355_8b2f7a3065_c.jpg");


//		verify(urlLoadQueue).putInt("http://farm2.staticflickr.com/1524/24897666730_1af4b3e837_c.jpg", 0);
//		verify(urlLoadQueue).putInt("http://farm2.staticflickr.com/1624/25193301355_8b2f7a3065_c.jpg", 1);
//		verify(urlLoadQueue, times(numItems)).putInt(anyString(), anyInt());
	}

	private int getNumberOfItems(JSONObject jsonObject) throws JSONException {
		return jsonObject.getJSONArray("items").length();
	}

	public String readJsonFromFile() throws IOException {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("ActMainTestJsonValid.json");
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}
}
