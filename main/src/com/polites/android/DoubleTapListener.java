package com.polites.android;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class DoubleTapListener extends SimpleOnGestureListener {

	private GestureImageView image;
	private GestureImageViewTouchListener touchListener;
	
	public DoubleTapListener(GestureImageView image, GestureImageViewTouchListener touchListener) {
		super();
		this.image = image;
		this.touchListener = touchListener;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		if(image.getScale() == touchListener.getStartingScale())
			image.setScale(touchListener.getDoubleTapScale());
		else
			image.reset();
		
		return true;
	}
}
