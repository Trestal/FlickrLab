package com.lab.flickr.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import com.lab.flickr.R;
import com.lab.flickr.Util.FileUtils;
import com.lab.flickr.fragments.adapters.GalleryPagerAdapter;
import com.lab.flickr.fragments.adapters.RecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Matt on 18/02/2016.
 */
public class FragMain extends Fragment implements ViewPager.OnPageChangeListener {

	private ViewPager viewPager;
	private RecyclerView recyclerView;

	private GalleryPagerAdapter galleryPagerAdapter;
	private RecyclerViewAdapter recyclerViewAdapter;

	private ArrayList<Bitmap> data = new ArrayList<>();

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		File dir = new File(getActivity().getFilesDir().getAbsolutePath() + FileUtils.INTERNAL_PATH);
		if (dir.exists()) {
			Log.d("FragMain", "onCreate : number of images : " + dir.listFiles().length);
			for (File file : dir.listFiles()) {
				if (file.getName().contains(".jpg")) {
					data.add(FileUtils.loadBitmapFromFile(getActivity(), file.getAbsolutePath()));
				}
			}
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_main, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		viewPager = (ViewPager) view.findViewById(R.id.frag_main_viewPager);
		recyclerView = (RecyclerView) view.findViewById(R.id.frag_main_recyclerView);
		galleryPagerAdapter = new GalleryPagerAdapter(getActivity(), data);
		viewPager.setAdapter(galleryPagerAdapter);
		viewPager.addOnPageChangeListener(this);
		recyclerViewAdapter = new RecyclerViewAdapter(data);
		recyclerView.setAdapter(recyclerViewAdapter);
		int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
		if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
			recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
		} else if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
			recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {

	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}
}
