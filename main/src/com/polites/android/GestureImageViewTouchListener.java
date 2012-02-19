package com.polites.android;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class GestureImageViewTouchListener implements OnTouchListener {
	
	private GestureImageView image;
	
	private float currentX = 0, currentY = 0, lastX = 0, lastY = 0, nextX = 0, nextY = 0;
	
	boolean touched = false;
	
	private float lastDistance;
	private float lastScale = 1.0f;
	private float currentScale = 1.0f;
	
	private float boundaryLeft = 0;
	private float boundaryTop = 0;
	private float boundaryRight = 0;
	private float boundaryBottom = 0;
	
	private float maxScale = 5.0f;
	private float minScale = 0.25f;
	
	private float centerX = 0;
	private float centerY = 0;
	
	private float startingScale = 0;
	
	private boolean canDragX = false;
	private boolean canDragY = false;
	
	private boolean multiTouch = false;
	
	private int displayWidth;
	private int displayHeight;
	
	private int imageWidth;
	private int imageHeight;
	
	private GestureListener gListener;
	private GestureDetector gDetector;
	private GestureImageViewListener imageListener;

	public GestureImageViewTouchListener(GestureImageView image, int displayWidth, int displayHeight) {
		super();
		
		this.image = image;
		
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;
		
		this.centerX = (float) displayWidth / 2.0f;
		this.centerY = (float) displayHeight / 2.0f;
		
		this.imageWidth = image.getImageWidth();
		this.imageHeight = image.getImageHeight();
		
		startingScale = image.getScale();
		
		currentScale = startingScale;
		lastScale = startingScale;
		
		// Calc boundaries
		boundaryRight = displayWidth;
		boundaryBottom = displayHeight;
		boundaryLeft = 0;
		boundaryTop = 0;
		
		nextX = image.getX();
		nextY = image.getY();
		
		gListener = new GestureListener(image);
		gDetector = new GestureDetector(gListener);
		imageListener = image.getGestureImageViewListener();
		
		calculateBoundaries();
	}
	

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		if(gDetector.onTouchEvent(event)) {
			lastDistance = 0;
			lastScale = startingScale;
			currentScale = startingScale;
			nextX = image.getX();
			nextY = image.getY();
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP) {
			
			multiTouch = false;
			
			lastDistance = 0;
			lastScale = currentScale;
			
			if(!canDragX) {
				nextX = centerX;
			}
			
			if(!canDragY) {
				nextY = centerY;
			}
			
			boundCoordinates();
			
			if(!canDragX && !canDragY) {
				currentScale = startingScale;
				lastScale = currentScale;
			}
			
			image.setScale(currentScale);
			image.setPosition(nextX, nextY);
			
			if(imageListener != null) {
				imageListener.onScale(currentScale);
				imageListener.onPosition(nextX, nextY);
			}			
			
			image.redraw();
		}
		else if(event.getAction() == MotionEvent.ACTION_DOWN) {
			lastX = event.getX();
			lastY = event.getY();
			
			if(imageListener != null) {
				imageListener.onTouch(lastX, lastY);
			}
			touched = true;
		}
		else if(event.getAction() == MotionEvent.ACTION_MOVE) {
			if(event.getPointerCount() > 1) {
				multiTouch = true;
				if(lastDistance > 0) {
					float distance = MathUtils.distance(event);
					currentScale = (distance / lastDistance) * lastScale;
					
					if(currentScale > maxScale) {
						currentScale = maxScale;	
					}
					else if (currentScale < minScale) {
						currentScale = minScale;
					}
					
					calculateBoundaries();
//					midpoint(event);
					image.setScale(currentScale);
					
					if(imageListener != null) {
						imageListener.onScale(currentScale);
					}						
					
					image.redraw();
				}
				else {
					lastDistance = MathUtils.distance(event);
				}
			}
			else {
				if(!touched) {
					touched = true;
					lastX = event.getX();
					lastY = event.getY();
					nextX = image.getX();
					nextY = image.getY();
				}
				else if(!multiTouch) {
					if(handleDrag(event.getX(), event.getY())) {
						image.redraw();
					}
				}
			}
		}
		return true;
	}
	
	protected boolean handleDrag(float x, float y) {
		currentX = x;
		currentY = y;
		
		float diffX = (currentX - lastX);
		float diffY = (currentY - lastY);
		
		if(diffX != 0 || diffY != 0) {
			
			if(canDragX) nextX += diffX;
			if(canDragY) nextY += diffY;
			
			boundCoordinates();
			
			lastX = currentX;
			lastY = currentY;
			
			if(canDragX || canDragY) {
				image.setPosition(nextX, nextY);
				
				if(imageListener != null) {
					imageListener.onPosition(nextX, nextY);
				}					
				
				return true;
			}
		}
		
		return false;
	}
	
	public void reset() {
		currentScale = startingScale;
		nextX = centerX;
		nextY = centerY;
		calculateBoundaries();
		image.setScale(currentScale);
		image.setPosition(nextX, nextY);
		image.redraw();
	}
	
	
	public float getMaxScale() {
		return maxScale;
	}

	public void setMaxScale(float maxScale) {
		this.maxScale = maxScale;
	}

	public float getMinScale() {
		return minScale;
	}

	public void setMinScale(float minScale) {
		this.minScale = minScale;
	}

	protected void boundCoordinates() {
		if(nextX < boundaryLeft) {
			nextX = boundaryLeft;
		}
		else if(nextX > boundaryRight) {
			nextX = boundaryRight;
		}

		if(nextY < boundaryTop) { 
			nextY = boundaryTop;
		}
		else if(nextY > boundaryBottom) {
			nextY = boundaryBottom;
		}
	}
	
	protected void calculateBoundaries() {
		
		int effectiveWidth = Math.round( (float) imageWidth * currentScale );
		int effectiveHeight = Math.round( (float) imageHeight * currentScale );
		
		canDragX = effectiveWidth > displayWidth;
		canDragY = effectiveHeight > displayHeight;
		
		if(canDragX) {
			float diff = (float)(effectiveWidth - displayWidth) / 2.0f;
			boundaryLeft = centerX - diff;
			boundaryRight = centerX + diff;
		}
		
		if(canDragY) {
			float diff = (float)(effectiveHeight - displayHeight) / 2.0f;
			boundaryTop = centerY - diff;
			boundaryBottom = centerY + diff;
		}
	}
}
