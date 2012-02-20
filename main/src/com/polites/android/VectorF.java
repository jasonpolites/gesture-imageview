package com.polites.android;

import android.graphics.PointF;
import android.view.MotionEvent;

public class VectorF {
	
	public float angle;
	public float length;
	
	public final PointF start = new PointF();
	public final PointF end = new PointF();
	
	public void calculateEndPoint() {
		end.x = (float) Math.cos(angle) * length + start.x;
		end.y = (float) Math.sin(angle) * length + start.y;
	}
	
	public void setStart(PointF p) {
		this.start.x = p.x;
		this.start.y = p.y;
	}
	
	public void setEnd(PointF p) {
		this.end.x = p.x;
		this.end.y = p.y;
	}
	
	public void set(MotionEvent event) {
		this.start.x = event.getX(0);
		this.start.y = event.getY(0);
		this.end.x = event.getX(1);
		this.end.y = event.getY(1);
	}
	
	public void calculateLength() {
		length = MathUtils.distance(start, end);
	}
	
	public void calculateAngle() {
		angle = MathUtils.angle(start, end);
	}
}
