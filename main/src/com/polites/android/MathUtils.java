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
import android.util.FloatMath;
import android.view.MotionEvent;

public class MathUtils {
	
	public static float distance(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}
	
	public static float distance(PointF p1, PointF p2) {
		float x = p1.x - p2.x;
		float y = p1.y - p2.y;
		return FloatMath.sqrt(x * x + y * y);
	}
	
	public static float distance(float x1, float y1, float x2, float y2) {
		float x = x1 - x2;
		float y = y1 - y2;
		return FloatMath.sqrt(x * x + y * y);
	}

	public static void midpoint(MotionEvent event, PointF point) {
		float x1 = event.getX(0);
		float y1 = event.getY(0);
		float x2 = event.getX(1);
		float y2 = event.getY(1);
		midpoint(x1, y1, x2, y2, point);
	}

	public static void midpoint(float x1, float y1, float x2, float y2, PointF point) {
		point.x = (x1 + x2) / 2.0f;
		point.y = (y1 + y2) / 2.0f;
	}
	/**
	 * Rotates p1 around p2 by angle degrees.
	 * @param p1
	 * @param p2
	 * @param angle
	 */
	public void rotate(PointF p1, PointF p2, float angle) {
		float px = p1.x;
		float py = p1.y;
		float ox = p2.x;
		float oy = p2.y;
		p1.x = (float) (Math.cos(angle) * (px-ox) - Math.sin(angle) * (py-oy) + ox);
		p1.y = (float) (Math.sin(angle) * (px-ox) + Math.cos(angle) * (py-oy) + oy);
	}
	
	public static float angle(PointF p1, PointF p2) {
		return angle(p1.x, p1.y, p2.x, p2.y);
	}	
	
	public static float angle(float x1, float y1, float x2, float y2) {
		return (float) Math.atan2(y2 - y1, x2 - x1);
	}
}
