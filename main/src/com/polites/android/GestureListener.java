package com.polites.android;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class GestureListener extends SimpleOnGestureListener {

	private GestureImageView image;
	
	public GestureListener(GestureImageView image) {
		super();
		this.image = image;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		image.reset();
		return true;
	}
}
