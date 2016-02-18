package com.lab.flickr.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.lab.flickr.R;
import com.lab.flickr.fragments.FragJsonLoader;
import com.lab.flickr.network.JsonLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Matt on 18/02/2016.
 */
public class ActMain extends Activity implements DialogInterface.OnDismissListener, JsonLoader.JsonLoaderListener {

	private ProgressDialog progressDialog;

	private Bundle urlLoadQueue = new Bundle(); //Remains empty if there are no new urls to load
	private boolean reloadUrlsTrigger = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		// Load all the bitmaps and save to file if this activity hasn't been loaded yet
		if (savedInstanceState == null) {
			downloadPhotos();
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (progressDialog.isShowing()) {

		}
		//TODO unregister receives, cancel AsyncTasks
	}

	public void downloadPhotos() {
		if (isNetworkAvailable()) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setMessage(getString(R.string.act_main_progress_dialog_message));
			progressDialog.setOnDismissListener(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setIndeterminate(true);
			progressDialog.setProgress(0);
			progressDialog.show();

			if (urlLoadQueue.isEmpty() || reloadUrlsTrigger) {
				loadJson();
			}
		}
	}

	private void loadJson() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		FragJsonLoader jsonLoader = new FragJsonLoader(); //performLoadingTask is called from onStart in the fragment
		jsonLoader.setLoaderListener(this);
		ft.add(jsonLoader, getResources().getString(R.string.frag_json_loader_tag));
		ft.commit();
		Log.d("ActMain/loadJson", "loadJson() Method - loader fragment created");
	}

	@Override
	public void onRequestFinished(JSONObject jsonObject) {
		FragmentManager fm = getFragmentManager();
		fm.beginTransaction().remove(fm.findFragmentByTag(getResources().getString(R.string.frag_json_loader_tag))).commit();
		Log.d("ActMain/onRequestFinis", "Retrieved jsonObject. Is null ? : " + (jsonObject == null));
		extractImageUrls(jsonObject);
		progressDialog.dismiss();
		for (String string : urlLoadQueue.keySet()) {
			Log.d("ActMain", string + " ----- " + urlLoadQueue.getInt(string));
		}
		//All images are loaded asynchronously. Therefore we will add all the view templates before they are loaded
		//as they load, we will update the imageView bitmap at that position
//		galleryPagerAdapter.addImageTemplates(this.urlsList.size());
//		loadNextImage();
	}

	private void extractImageUrls(JSONObject jsonObject) {
		try {
			JSONArray items = jsonObject.getJSONArray("items");
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
