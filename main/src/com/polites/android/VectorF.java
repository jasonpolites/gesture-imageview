/*
 * Copyright (c) 2012 Jason Polites
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
