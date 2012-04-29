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

/**
 * @author Jason Polites
 *
 */
public class FlingAnimation implements Animation {
	
	private float velocityX;
	private float velocityY;
	
	private float factor = 0.85f;
	
	private float threshold = 10;
	
	private FlingAnimationListener listener;
	
	/* (non-Javadoc)
	 * @see com.polites.android.Transformer#update(com.polites.android.GestureImageView, long)
	 */
	@Override
	public boolean update(GestureImageView view, long time) {
		float seconds = (float) time / 1000.0f;
		
		float dx = velocityX * seconds;
		float dy = velocityY * seconds;
		
		velocityX *= factor;
		velocityY *= factor;
		
		if(listener != null) {
			listener.onMove(dx, dy);
		}
		
		return (Math.abs(velocityX) > threshold && Math.abs(velocityY) > threshold);
	}
	
	public void setVelocityX(float velocityX) {
		this.velocityX = velocityX;
	}
	
	public void setVelocityY(float velocityY) {
		this.velocityY = velocityY;
	}
	
	public void setFactor(float factor) {
		this.factor = factor;
	}

	public void setListener(FlingAnimationListener listener) {
		this.listener = listener;
	}
}
