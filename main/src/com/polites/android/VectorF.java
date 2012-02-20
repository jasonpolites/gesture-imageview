package com.polites.android;

import android.graphics.PointF;

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
	
	public void calculateLength() {
		length = MathUtils.distance(start, end);
	}
	
	public void calculateAngle() {
		angle = MathUtils.angle(start, end);
	}
}
