package com.lab.flickr.fragments.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.lab.flickr.R;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

	private ImageView imageView;

	public RecyclerViewHolder(View itemView) {
		super(itemView);
		this.imageView = (ImageView) itemView.findViewById(R.id.frag_main_recyclerView_imageView);
	}

	public ImageView getImageView() {
		return imageView;
	}
}
