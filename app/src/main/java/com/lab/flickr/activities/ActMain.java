package com.lab.flickr.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.lab.flickr.R;

/**
 * Created by Matt on 18/02/2016.
 */
public class ActMain extends Activity implements DialogInterface.OnDismissListener{

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		// Load all the bitmaps and save to file if this activity hasn't been loaded yet
		if (savedInstanceState == null) {
			loadPhotos();
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {

	}

	public void loadPhotos() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getString(R.string.act_main_progress_dialog_message));
		progressDialog.setOnDismissListener(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setIndeterminate(true);
		progressDialog.setProgress(0);
		progressDialog.show();
	}
}
