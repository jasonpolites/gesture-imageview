package com.polites.android;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class DoubleTapListener extends SimpleOnGestureListener {

	private GestureImageView image;
	
	public DoubleTapListener(GestureImageView image) {
		super();
		this.image = image;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		image.reset();
		return true;
	}
}
