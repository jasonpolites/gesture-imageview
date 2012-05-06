/*
 * Copyright (c) 2011 Socialize Inc.
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
public class MoveAnimation implements Animation {

	private boolean firstFrame = true;
	
	private float startX;
	private float startY;
	
	private float targetX;
	private float targetY;
	private long animationTimeMS = 100;
	private long totalTime = 0;
	
	private MoveAnimationListener moveAnimationListener;
	
	/* (non-Javadoc)
	 * @see com.polites.android.Animation#update(com.polites.android.GestureImageView, long)
	 */
	@Override
	public boolean update(GestureImageView view, long time) {
		totalTime += time;
		
		if(firstFrame) {
			firstFrame = false;
			startX = view.getImageX();
			startY = view.getImageY();
		}
		
		if(totalTime < animationTimeMS) {
			
			float ratio = (float) totalTime / animationTimeMS;
			
			float newX = ((targetX - startX) * ratio) + startX;
			float newY = ((targetY - startY) * ratio) + startY;
			
			if(moveAnimationListener != null) {
				moveAnimationListener.onMove(newX, newY);
			}
			
			return true;
		}
		else {
			if(moveAnimationListener != null) {
				moveAnimationListener.onMove(targetX, targetY);
			}
		}
		
		return false;
	}

	public void reset() {
		firstFrame = true;
		totalTime = 0;
	}

	
	public float getTargetX() {
		return targetX;
	}

	
	public void setTargetX(float targetX) {
		this.targetX = targetX;
	}

	
	public float getTargetY() {
		return targetY;
	}
	
	public void setTargetY(float targetY) {
		this.targetY = targetY;
	}
	
	public long getAnimationTimeMS() {
		return animationTimeMS;
	}

	public void setAnimationTimeMS(long animationTimeMS) {
		this.animationTimeMS = animationTimeMS;
	}
	
	public void setMoveAnimationListener(MoveAnimationListener moveAnimationListener) {
		this.moveAnimationListener = moveAnimationListener;
	}
}
