package com.lab.flickr.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.lab.flickr.R;
import com.lab.flickr.Util.FileUtils;
import com.lab.flickr.Util.Security;
import com.lab.flickr.fragments.FragImageLoader;
import com.lab.flickr.fragments.FragJsonLoader;
import com.lab.flickr.fragments.FragMain;
import com.lab.flickr.network.DataWrapper;
import com.lab.flickr.network.ImageLoader;
import com.lab.flickr.network.JsonLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Matt on 18/02/2016.
 */
public class ActMain extends Activity implements DialogInterface.OnDismissListener, JsonLoader.JsonLoaderListener, ImageLoader.ImageLoaderListener {

	private static final String LOAD_NEW_IMAGE = "LOAD_NEW_IMAGE";

	private ProgressDialog progressDialog;
	private Toolbar toolbar;

	private Bundle urlLoadQueue = new Bundle(); //Remains empty if there are no new urls to load

	private BroadcastReceiver loadNewImageReceiver =  new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(LOAD_NEW_IMAGE)) {
				loadImage();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		initiateToolbar();
		// Load all the bitmaps and save to file if this activity hasn't been loaded yet
		if (savedInstanceState == null) {
			startDownload();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.toolbar, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean consume = false;
		switch (item.getItemId()) {
			case R.id.toolbar_refresh : {
				cleanUp();
				startDownload();
				consume = true;
			}
		}
		return consume;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		cleanUp();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		Fragment mainFragment = new FragMain();
		if (!mainFragment.isVisible()) {
			FragmentManager fm = getFragmentManager();
			fm.beginTransaction().add(R.id.act_main_container, mainFragment, getString(R.string.frag_main_tag)).commit();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cleanUp();
	}

	@Override
	public void onRequestFinished(JSONObject jsonObject) {
		Log.d("ActMain", "onRequestFinished - Retrieved jsonObject. Is null ? : " + (jsonObject == null));
		FragmentManager fm = getFragmentManager();
		fm.beginTransaction().remove(fm.findFragmentByTag(getResources().getString(R.string.frag_json_loader_tag))).commit();
		extractImageUrls(jsonObject);
		progressDialog.setMax(urlLoadQueue.size());
		loadImage();
	}

	@Override
	public void onRequestFinished(DataWrapper dataWrapper) {
		Log.d("ActMain", "onRequestFinished : Datawrapper is null ? " + (dataWrapper == null));
		if (dataWrapper != null && dataWrapper.getBitmap() != null) {
			urlLoadQueue.remove(dataWrapper.getUrl());
			int progress = progressDialog.getMax() - urlLoadQueue.size();
			progressDialog.setProgress(progress);
			//URLs contain a lot of invalid file name chars so we compute an MD5 of the URL for a fairly random fileName that
			//may be easily reproduced if needed. The index is appended to the front followed by a dot followed by the hash
			String fileName = "" + dataWrapper.getIndex() + "." + Security.computeMD5(dataWrapper.getUrl());
			FileUtils.saveJpegToFile(this, dataWrapper.getBitmap(), fileName);
		}
		FragmentManager fm = getFragmentManager();
		fm.beginTransaction().remove(fm.findFragmentByTag(getResources().getString(R.string.frag_image_loader_tag))).commit();
		if (urlLoadQueue.size() > 0) {
			LocalBroadcastManager.getInstance(this).sendBroadcastSync(new Intent(LOAD_NEW_IMAGE));
		} else {
			progressDialog.dismiss();
		}
	}

	private void initiateToolbar() {
		toolbar = (Toolbar) findViewById(R.id.act_main_toolbar);
		toolbar.setBackgroundColor(Color.DKGRAY);
//		toolbar.inflateMenu(R.menu.toolbar);
		setActionBar(toolbar);
	}

	public void startDownload () {
		if (isNetworkAvailable()) {
			FragmentManager fm = getFragmentManager();
			Fragment fragMain = fm.findFragmentByTag(getString(R.string.frag_main_tag));
			if(fragMain != null) {
				fm.beginTransaction().remove(fragMain).commit();
			}
			urlLoadQueue.clear();
			FileUtils.deleteDirectoryContents(getFilesDir().getAbsolutePath() + FileUtils.INTERNAL_PATH);
			//Need to destroy the main fragment view, clear the queue, remove and stop any loading tasks and start it again with a new progress dialog
			loadJson();
			LocalBroadcastManager.getInstance(this).registerReceiver(loadNewImageReceiver, new IntentFilter(LOAD_NEW_IMAGE));
			//Prevent screen rotation while the processDialog is up. This is reset in onDismiss®
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
			progressDialog = new ProgressDialog(this);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setMessage(getString(R.string.act_main_progress_dialog_message));
			progressDialog.setOnDismissListener(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setIndeterminate(false);
			progressDialog.setProgress(0);
			progressDialog.show();
		} else {
			Toast.makeText(this, "Network not available. Please check connection", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Destroy async fragments
	 * Unregister receivers
	 */
	private void cleanUp() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(loadNewImageReceiver);
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		try {
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			FragJsonLoader jsonLoader = (FragJsonLoader) fm.findFragmentByTag(getString(R.string.frag_json_loader_tag));
			if (jsonLoader != null) {
				jsonLoader.cancel(true);
				ft.remove(jsonLoader);
			}
			FragImageLoader imageLoader = (FragImageLoader) fm.findFragmentByTag(getString(R.string.frag_image_loader_tag));
			if (imageLoader != null) {
				imageLoader.cancel(true);
				ft.remove(imageLoader);
			}
			ft.commit();
		} catch (IllegalStateException e) {
			Log.d("ActMain", "cleanUp() : IllegalStateException : Called FragmentTransaction commit() after onSavedInstanceState");
		}
	}

	/**
	 * Downloads the json from a url stored in strings.xml in a new Fragment that runs an AsyncTask
	 */
	private void loadJson() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		FragJsonLoader jsonLoader = new FragJsonLoader(); //performLoadingTask is called from onStart in the fragment
		jsonLoader.setLoaderListener(this);
		ft.add(jsonLoader, getResources().getString(R.string.frag_json_loader_tag));
		ft.commit();
		Log.d("ActMain", "loadJson - loader fragment created");
	}

	/**
	 * Creates a new ui-less Fragment that starts an AsyncTask. This then downloads an image from the url in the DataWrapper
	 */
	private void loadImage() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		FragImageLoader imageLoader = new FragImageLoader();
		imageLoader.setLoaderListener(this);
		String url = urlLoadQueue.keySet().toArray()[0].toString();
		int index = urlLoadQueue.getInt(url);
		Bundle bundle = new Bundle();
		bundle.putParcelable(DataWrapper.PARCEL_KEY, new DataWrapper(index, url));
		imageLoader.setArguments(bundle);
		ft.add(imageLoader, getString(R.string.frag_image_loader_tag));
		ft.commit();
	}

	private void extractImageUrls(JSONObject jsonObject) {
		try {
			JSONArray items = jsonObject.getJSONArray("items");
			Log.d("ActMain","extractImageUrls - Number of images added to queue : " + items.length());
			for (int i = 0, ii = items.length(); i < ii; i++) {
				String url = items.getJSONObject(i).getJSONObject("media").getString("m");
				url = url.replace("m.jpg", "c.jpg"); //Retrieve higher resolution image
				url = url.replace("https://", "http://");
				this.urlLoadQueue.putInt(url, i);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
