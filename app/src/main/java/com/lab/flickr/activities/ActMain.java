package com.lab.flickr.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.widget.Toast;

import com.lab.flickr.R;
import com.lab.flickr.Util.FileUtils;
import com.lab.flickr.fragments.FragImageLoader;
import com.lab.flickr.fragments.FragJsonLoader;
import com.lab.flickr.fragments.FragMain;
import com.lab.flickr.network.DataWrapper;
import com.lab.flickr.network.Loader;
import com.lab.flickr.network.Loader.LoaderType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ActMain extends AppCompatActivity implements DialogInterface.OnDismissListener, Loader.LoaderListener {

	private ProgressDialog progressDialog;

	private BlockingQueue<DataWrapper> queue = new LinkedBlockingQueue<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		initiateToolbar();
		initiateProgressBar();
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
			case R.id.toolbar_refresh: {
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
		//TODO Find frag first. If null create new one
		Fragment mainFragment = new FragMain();
		if (!mainFragment.isVisible()) {
			FragmentManager fm = getFragmentManager();
			fm.beginTransaction().add(R.id.act_main_container, mainFragment, getString(R.string.frag_main_tag)).commit();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		cleanUp();
	}

	private void initiateToolbar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.act_main_toolbar);
		toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar));
		setSupportActionBar(toolbar);
	}

	private void initiateProgressBar() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage(getString(R.string.act_main_progress_dialog_message));
		progressDialog.setOnDismissListener(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setIndeterminate(false);
	}

	/**
	 * Starts a fresh download with new images
	 */
	private void startDownload() {
		if (isNetworkAvailable()) {
			FragmentManager fm = getFragmentManager();
			Fragment fragMain = fm.findFragmentByTag(getString(R.string.frag_main_tag));
			if (fragMain != null) {
				fm.beginTransaction().remove(fragMain).commit();
			}
			queue.clear();
			FileUtils.deleteDirectoryContents(getFilesDir().getAbsolutePath() + FileUtils.INTERNAL_PATH);
			//Need to destroy the main fragment view, clear the queue, remove and stop any loading tasks and start it again with a new progress dialog
			showProgress();
			loadJson();
		} else {
			Toast.makeText(this, getString(R.string.act_main_no_network), Toast.LENGTH_SHORT).show();
		}
	}

	private void showProgress() {
		//Prevent screen rotation while the processDialog is up. This is reset in onDismiss
		int rotation = getWindowManager().getDefaultDisplay().getRotation();
		if (rotation == Surface.ROTATION_0) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else if (rotation == Surface.ROTATION_90) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else if (rotation == Surface.ROTATION_270) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
		} else if (rotation == Surface.ROTATION_180) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
		}
		progressDialog.show();
		progressDialog.setProgress(0);
	}

	/**
	 * Destroy async fragments
	 * Unregister receivers
	 */
	private void cleanUp() {
		if (progressDialog.isShowing()) {
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
			Log.e("ActMain", "cleanUp() : IllegalStateException : Called FragmentTransaction commit() after onSavedInstanceState");
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
		Bundle bundle = new Bundle();
		DataWrapper wrapper = new DataWrapper();
		wrapper.setValue(DataWrapper.Key.URL, getResources().getString(R.string.frag_json_loader_url));
		bundle.putParcelable(DataWrapper.Key.PARCEL_KEY.name(), wrapper);
		jsonLoader.setArguments(bundle);
		ft.add(jsonLoader, getResources().getString(R.string.frag_json_loader_tag));
		ft.commit();
	}

	/**
	 * Creates a new ui-less Fragment that starts an AsyncTask. This then downloads an image from the url in the DataWrapper
	 */
	private void loadImage() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		FragImageLoader imageLoader = new FragImageLoader();
		imageLoader.setLoaderListener(this);

		Bundle bundle = new Bundle();
		DataWrapper wrapper = new DataWrapper();
		wrapper.setValue(DataWrapper.Key.QUEUE, queue);
		bundle.putParcelable(DataWrapper.Key.PARCEL_KEY.name(), wrapper);
		imageLoader.setArguments(bundle);
		ft.add(imageLoader, getString(R.string.frag_image_loader_tag));
		ft.commit();
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	@Override
	public synchronized void reportProgress(LoaderType type, Integer progress) {
		switch (type) {
			case JsonLoader:
				break;
			case ImageLoader:
				progressDialog.setProgress(progressDialog.getProgress() + progress);
				break;
		}
	}

	@Override
	public synchronized void onRequestFinished(LoaderType type, DataWrapper result) {
		switch (type) {
			case JsonLoader:
				finishJsonLoader(result);
				break;
			case ImageLoader:
				finishImageLoader();
				break;
		}
	}

	private void finishJsonLoader(DataWrapper wrapper) {
		FragmentManager fm = getFragmentManager();
		fm.beginTransaction().remove(fm.findFragmentByTag(getResources().getString(R.string.frag_json_loader_tag))).commit();
		JSONObject jsonObject = (JSONObject) wrapper.getValue(DataWrapper.Key.JSON);
		populateLoadQueue(jsonObject);
		progressDialog.setMax(queue.size() - 1); //1 less due to final element being a poison pill
		loadImage();

	}

	private void finishImageLoader() {
		FragmentManager fm = getFragmentManager();
		fm.beginTransaction().remove(fm.findFragmentByTag(getResources().getString(R.string.frag_image_loader_tag))).commit();
		progressDialog.dismiss();
		//MainFrag is init in onDismiss. This is because the dialog can be dismissed early by the user.
		//The main fragment will then display all images that had been loaded to that point
	}

	private void populateLoadQueue(JSONObject jsonObject) {
		try {
			JSONArray items = jsonObject.getJSONArray("items");
			for (int i = 0, ii = items.length(); i < ii; i++) {
				String url = items.getJSONObject(i).getJSONObject("media").getString("m");
				url = url.replace("m.jpg", "c.jpg"); //Retrieve higher resolution image
				url = url.replace("https://", "http://");
				DataWrapper wrapper = new DataWrapper();
				wrapper.setValue(DataWrapper.Key.URL, url);
				wrapper.setValue(DataWrapper.Key.INDEX, i);
				queue.add(wrapper);
			}
			DataWrapper pill = new DataWrapper();
			pill.setValue(DataWrapper.Key.POISON_PILL, null);
			queue.add(pill);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
