package com.lab.flickr.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.lab.flickr.fragments.interfaces.RecyclerViewOnItemClickListener;

import java.io.File;
import java.util.ArrayList;

public class FragMain extends Fragment implements ViewPager.OnPageChangeListener, RecyclerViewOnItemClickListener {

	private ViewPager viewPager;
	private RecyclerView recyclerView;

	private GalleryPagerAdapter galleryPagerAdapter;
	private RecyclerViewAdapter recyclerViewAdapter;

	private LinearLayoutManager recyclerLayoutManager;

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
		recyclerViewAdapter = new RecyclerViewAdapter(data, this);
		recyclerView.setAdapter(recyclerViewAdapter);
		int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
		if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
			recyclerLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
		} else if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
			recyclerLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
		}
		recyclerView.setLayoutManager(recyclerLayoutManager);
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
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
		if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) { //port
			horizontalScroll(position);
		} else if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) { //land
			verticalScroll(position);
		}
	}

	private void verticalScroll(int position) {
		View view = recyclerView.getChildAt(0); //Only used to get the width of a view. They are all the same so this is safe
		if (view != null) {
			int width = view.getHeight();
			float pos = recyclerView.computeVerticalScrollOffset();
			float targetPos = position * width;
			float delta = (pos - targetPos) * -1;
			recyclerView.smoothScrollBy(0, (int) delta);
		}
	}

	private void horizontalScroll(int position) {
		View view = recyclerView.getChildAt(0); //Only used to get the width of a view. They are all the same so this is safe
		if (view != null) {
			int width = view.getWidth();
			float pos = recyclerView.computeHorizontalScrollOffset();
			float targetPos = position * width;
			float delta = (pos - targetPos) * -1;
			recyclerView.smoothScrollBy((int) delta, 0);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	@Override
	public void onClick(View view, int position) {
		viewPager.setCurrentItem(position);
	}
}
