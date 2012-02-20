package com.polites.android;

import android.graphics.PointF;

public class NullPointF extends PointF {

	boolean nil = true;
	
	public void setNull() {
		nil = true;
	}
	
	public void setNotNull() {
		nil = false;
	}
	
	public boolean isNull() {
		return nil;
	}
	
	public void copy(PointF p) {
		this.x = p.x;
		this.y = p.y;
		nil = false;
	}
	
	public void reset() {
		this.x = 0;
		this.y = 0;
	}
	
	public void scale(float scale) {
		this.x *= scale;
		this.y *= scale;
	}
}
