package com.polites.android;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GestureImageView extends View  {
	
	public static final String GLOBAL_NS = "http://schemas.android.com/apk/res/android";
	public static final String LOCAL_NS = "http://schemas.polites.com/android";
	
	private final Semaphore drawLock = new Semaphore(0);
	private Animator animator;
	
	private Bitmap bitmap;
	
	private Paint paint;

	private float x = 0, y = 0;
	
	private boolean layout = false;
	private boolean viewSet = false;
	
	private float scaleAdjust = 1.0f;
	private float startingScale = 1.0f;

	private float scale = 1.0f;
	private float maxScale = 5.0f;
	private float minScale = 0.25f;
	
	private float rotation = 0.0f;
	
	private int lastOrientation = -1;
	
	private float centerX;
	private float centerY;
	
	private float hWidth;
	private float hHeight;
	
	private int resId = -1;
	private boolean recycle = false;
	
	private int displayHeight;
	private int displayWidth;
	
	private GestureImageViewListener gestureImageViewListener;
	private GestureImageViewTouchListener gestureImageViewTouchListener;
	
	public GestureImageView(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
	}

	public GestureImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setImageResource(attrs.getAttributeResourceValue(GLOBAL_NS, "src", -1));
		setMinScale(attrs.getAttributeFloatValue(LOCAL_NS, "min-scale", minScale));
		setMaxScale(attrs.getAttributeFloatValue(LOCAL_NS, "max-scale", maxScale));
	}

	public GestureImageView(Context context) {
		super(context);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		int orientation = getResources().getConfiguration().orientation;
		
		if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
			displayHeight = MeasureSpec.getSize(heightMeasureSpec);
			float ratio = (float) this.bitmap.getWidth() / (float) this.bitmap.getHeight();
			displayWidth = Math.round( (float) displayHeight * ratio) ;
		}
		else {
			displayWidth = MeasureSpec.getSize(widthMeasureSpec);
			float ratio = (float) this.bitmap.getHeight() / (float) this.bitmap.getWidth();
			displayHeight = Math.round( (float) displayWidth * ratio) ;
		}
		
		setMeasuredDimension(displayWidth, displayHeight);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if(changed) {
			setupCanvas(displayWidth, displayHeight, getResources().getConfiguration().orientation);
		}
	}

	protected void setupCanvas(int measuredWidth, int measuredHeight, int orientation) {
		
		if(lastOrientation != orientation) {
			layout = false;
			lastOrientation = orientation;
		}
		
		if(bitmap != null && !layout) {
			
			int imageWidth = this.bitmap.getWidth();
			int imageHeight = this.bitmap.getHeight();

			hWidth = ((float)imageWidth / 2.0f);
			hHeight = ((float)imageHeight / 2.0f);
			
			if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
				displayHeight = measuredHeight;
				
				// Calc height based on width
				float ratio = (float) this.bitmap.getWidth() / (float) imageHeight;
				
				displayWidth = Math.round( (float) displayHeight * ratio);
				
				startingScale = (float) displayHeight / (float) imageHeight;
			}
			else {
				displayWidth = measuredWidth;
				
				// Calc height based on width
				float ratio = (float) this.bitmap.getHeight() / (float) imageWidth;
				
				displayHeight = Math.round( (float) displayWidth * ratio) ;
				
				startingScale = (float) displayWidth / (float) imageWidth;
			}
			
			scaleAdjust = startingScale;
			
			this.centerX = (float)measuredWidth / 2.0f;
			this.centerY = (float)measuredHeight / 2.0f;
			
			x = centerX;
			y = centerY;
			
			gestureImageViewTouchListener = new GestureImageViewTouchListener(this, measuredWidth, measuredHeight);
			gestureImageViewTouchListener.setMinScale(minScale);
			gestureImageViewTouchListener.setMaxScale(maxScale);
			
			setOnTouchListener(gestureImageViewTouchListener);	
			
			layout = true;
			viewSet = false;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(layout) {
			if(!viewSet) {
				canvas.setViewport(displayWidth, displayHeight);
				viewSet = true;
			}
			
			if(bitmap != null && !bitmap.isRecycled()) {
				canvas.save();
				
				float adjustedScale = scale * scaleAdjust;
				
				canvas.translate(x, y);
				
				if(rotation != 0.0f) {
					canvas.rotate(rotation);
				}
				
				if(adjustedScale != 1.0f) {
					canvas.scale(adjustedScale, adjustedScale);
				}
				
				canvas.drawBitmap(bitmap,-hWidth,-hHeight,paint);
				
				canvas.restore();
			}
			
			if(drawLock.availablePermits() <= 0) {
				drawLock.release();
			}
		}
	}
	
	/**
	 * Waits for a draw
	 * @param max time to wait for draw (ms)
	 * @throws InterruptedException
	 */
	public boolean waitForDraw(long timeout) throws InterruptedException {
		return drawLock.tryAcquire(timeout, TimeUnit.MILLISECONDS);
	}
	
	@Override
	protected void onAttachedToWindow() {
		animator = new Animator(this, "GestureImageViewAnimator");
		animator.start();
		
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);		
		
		if(resId >= 0 && bitmap == null) {
			setImageResource(resId);
		}
		
		super.onAttachedToWindow();
	}
	
	public void animationStart(Animation animation) {
		animator.play(animation);
	}
	
	public void animationStop() {
		animator.cancel();
	}

	@Override
	protected void onDetachedFromWindow() {
		if(animator != null) {
			animator.finish();
		}
		if(recycle && bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
		super.onDetachedFromWindow();
	}

	public void setImageBitmap(Bitmap image) {
		this.bitmap = image;
	}
	
	public void setImageResource(int id) {
		if(this.bitmap != null) {
			this.bitmap.recycle();
		}
		if(id >= 0) {
			this.recycle = true;
			this.resId = id;
			this.bitmap = BitmapFactory.decodeResource(getContext().getResources(), id);
		}
	}
	
	public int getImageWidth() {
		return (bitmap == null) ? 0 : bitmap.getWidth();
	}

	public int getImageHeight() {
		return  (bitmap == null) ? 0 : bitmap.getHeight();
	}
	
	public void moveBy(float x, float y) {
		this.x += x;
		this.y += y;
	}
	
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void redraw() {
		postInvalidate();
	}

	public void setMinScale(float min) {
		this.minScale = min;
		if(gestureImageViewTouchListener != null) {
			gestureImageViewTouchListener.setMinScale(min);
		}
	}
	
	public void setMaxScale(float max) {
		this.maxScale = max;
		if(gestureImageViewTouchListener != null) {
			gestureImageViewTouchListener.setMaxScale(max);
		}
	}
	
	public void setScale(float scale) {
		scaleAdjust = scale;
	}
	
	public float getScale() {
		return scaleAdjust;
	}
	
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public Paint getPaint() {
		return paint;
	}

	public void reset() {
		x = centerX;
		y = centerY;
		scaleAdjust = startingScale;
		redraw();
	}
	
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public void setGestureImageViewListener(GestureImageViewListener pinchImageViewListener) {
		this.gestureImageViewListener = pinchImageViewListener;
	}

	public GestureImageViewListener getGestureImageViewListener() {
		return gestureImageViewListener;
	}
}
