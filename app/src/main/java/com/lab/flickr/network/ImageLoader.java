package com.lab.flickr.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.lab.flickr.Util.FileUtils;
import com.lab.flickr.Util.JobRegister;
import com.lab.flickr.Util.JobRegister.Job;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ImageLoader extends Loader {

	private static final String TAG = ImageLoader.class.getSimpleName();

	private boolean poisonPill = false;
	private final Job job;

	public ImageLoader(Job job) {
		this.job = job;
	}

	@Override
	protected DataWrapper doInBackground(DataWrapper... params) {
		DataWrapper result = null;
		try {
			result = load(params[0]);
		} catch (InterruptedException e) {
			Log.e(this.getClass().getSimpleName(), "Thread exception: ", e);
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), "IOException: ", e);
		} catch (Exception e) {
			Log.wtf(this.getClass().getSimpleName(), "Major issue: ", e);
		}
		JobRegister.updateTaskState(job, getID(), true);
		while (!JobRegister.isAllTaskComplete(job)) {
			try {
				Log.d(this.getClass().getSimpleName(), "Thread : " + getID() + " Waiting for all tasks to finish");
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		return result;
	}

	@Override
	public LoaderType getType() {
		return LoaderType.ImageLoader;
	}

	@Override
	protected void onPostExecute(DataWrapper dataWrapper) {
		//Only call the callback from the poisoned thread when all threads have finished processing
		if (poisonPill) {
			super.onPostExecute(dataWrapper);
		}
	}

	private DataWrapper load(DataWrapper wrapper) throws InterruptedException, IOException {
		BlockingQueue<DataWrapper> queue = (BlockingQueue<DataWrapper>) wrapper.getValue(DataWrapper.Key.QUEUE);

		while (!queue.isEmpty() && !isCancelled()) {
			DataWrapper queueItem = queue.poll(50, TimeUnit.MILLISECONDS);
			if (queueItem != null && !isPoisonPill(queueItem)) {
				String urlString = queueItem.getValue(DataWrapper.Key.URL).toString();
				Log.d(this.getClass().getSimpleName(), "Thread : " + getID() + " Queue size : " + queue.size() + " URL : " + urlString);
				URL url = new URL(urlString);
				URLConnection conn = url.openConnection();
				conn.setConnectTimeout(10000); //timeout if image cannot be loaded in 10 seconds
				Bitmap bmp = BitmapFactory.decodeStream(conn.getInputStream());
				if (bmp != null) {
					queueItem.setValue(DataWrapper.Key.BITMAP, bmp);
					saveBitmap(queueItem);
				} else {
					StringBuilder err = new StringBuilder();
					err.append("Bitmap is null. Problem loading image from url : " + urlString + "\n");
					Log.d(TAG, err.toString());
				}
				publishProgress(1);
			}
		}
		return wrapper;
	}

	private boolean isPoisonPill(DataWrapper wrapper) {
		boolean isPill = wrapper.contains(DataWrapper.Key.POISON_PILL);
		if (isPill) {
			poisonPill = true;
		}
		return isPill;
	}

	private String createFileName(DataWrapper wrapper) {
		//hashcode is used because the url may contain characters that can't be used in a filename. Hashcode will produce the same code for the same input
		int index = (Integer) wrapper.getValue(DataWrapper.Key.INDEX);
		String url = wrapper.getValue(DataWrapper.Key.URL).toString();
		return "" + index + "." + url.hashCode();
	}

	private void saveBitmap(DataWrapper wrapper) {
		//URLs contain a lot of invalid file name chars so we compute an MD5 of the URL for a fairly random fileName that
		//may be easily reproduced if needed. The index is appended to the front followed by a dot followed by the hash
		Bitmap bmp = (Bitmap) wrapper.getValue(DataWrapper.Key.BITMAP);
		String fileName = createFileName(wrapper);
		FileUtils.saveJpegToFile(context, bmp, fileName);
	}
}
