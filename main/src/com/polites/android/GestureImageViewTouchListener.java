package com.polites.android;

import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class GestureImageViewTouchListener implements OnTouchListener {
	
	private GestureImageView image;
	
	private PointF current = new PointF();
	private PointF last = new PointF();
	private PointF next = new PointF();
	
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
	
	private final PointF midpoint = new PointF();
	
	private final VectorF lastVector = new VectorF();
	
	private DoubleTapListener doubleTapListener;
	private GestureDetector doubleTapDetector;
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
		
		next.x = image.getX();
		next.y = image.getY();
		
		doubleTapListener = new DoubleTapListener(image);
		doubleTapDetector = new GestureDetector(doubleTapListener);
		imageListener = image.getGestureImageViewListener();
		
		calculateBoundaries();
	}
	

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		if(doubleTapDetector.onTouchEvent(event)) {
			lastDistance = 0;
			lastScale = startingScale;
			currentScale = startingScale;
			next.x = image.getX();
			next.y = image.getY();
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP) {
			
			multiTouch = false;
			
			lastDistance = 0;
			lastScale = currentScale;
			
			if(!canDragX) {
				next.x = centerX;
			}
			
			if(!canDragY) {
				next.y = centerY;
			}
			
			boundCoordinates();
			
			if(!canDragX && !canDragY) {
				currentScale = startingScale;
				lastScale = currentScale;
			}
			
			image.setScale(currentScale);
			image.setPosition(next.x, next.y);
			
			if(imageListener != null) {
				imageListener.onScale(currentScale);
				imageListener.onPosition(next.x, next.y);
			}	
			
			image.redraw();
		}
		else if(event.getAction() == MotionEvent.ACTION_DOWN) {
			last.x = event.getX();
			last.y = event.getY();
			
			if(imageListener != null) {
				imageListener.onTouch(last.x, last.y);
			}
			
			touched = true;
		}
		else if(event.getAction() == MotionEvent.ACTION_MOVE) {
			if(event.getPointerCount() > 1) {
				multiTouch = true;
				if(lastDistance > 0) {
					float distance = MathUtils.distance(event);
					
					if(lastDistance != distance) {
						
						// We have moved (scaled)
						currentScale = (distance / lastDistance) * lastScale;
						
						if(currentScale > maxScale) {
							currentScale = maxScale;	
						}
						else if (currentScale < minScale) {
							currentScale = minScale;
						}
						
						calculateBoundaries();
						
						lastVector.length *= currentScale;
						
						lastVector.calculateEndPoint();
						
						lastVector.length /= currentScale;
						
						next.x = lastVector.end.x;
						next.y = lastVector.end.y;
						
						image.setScale(currentScale);
						image.setPosition(next.x, next.y);
						
						if(imageListener != null) {
							imageListener.onScale(currentScale);
							imageListener.onPosition(next.x, next.y);
						}
						
						image.redraw();
					}
				}
				else {
					lastDistance = MathUtils.distance(event);
					
					MathUtils.midpoint(event, midpoint);
					
					lastVector.setStart(midpoint);
					lastVector.setEnd(next);
					
					lastVector.calculateLength();
					lastVector.calculateAngle();
					
					lastVector.length /= lastScale;
				}
			}
			else {
				if(!touched) {
					touched = true;
					last.x = event.getX();
					last.y = event.getY();
					next.x = image.getX();
					next.y = image.getY();
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
		current.x = x;
		current.y = y;
		
		float diffX = (current.x - last.x);
		float diffY = (current.y - last.y);
		
		if(diffX != 0 || diffY != 0) {
			
			if(canDragX) next.x += diffX;
			if(canDragY) next.y += diffY;
			
			boundCoordinates();
			
			last.x = current.x;
			last.y = current.y;
			
			if(canDragX || canDragY) {
				image.setPosition(next.x, next.y);
				
				if(imageListener != null) {
					imageListener.onPosition(next.x, next.y);
				}					
				
				return true;
			}
		}
		
		return false;
	}
	
	public void reset() {
		currentScale = startingScale;
		next.x = centerX;
		next.y = centerY;
		calculateBoundaries();
		image.setScale(currentScale);
		image.setPosition(next.x, next.y);
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
		if(next.x < boundaryLeft) {
			next.x = boundaryLeft;
		}
		else if(next.x > boundaryRight) {
			next.x = boundaryRight;
		}

		if(next.y < boundaryTop) { 
			next.y = boundaryTop;
		}
		else if(next.y > boundaryBottom) {
			next.y = boundaryBottom;
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
