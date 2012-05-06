/*
 * Copyright (c) 2012 Jason Polites
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.polites.android;

import java.io.InputStream;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class GestureImageView extends ImageView  {

	public static final String GLOBAL_NS = "http://schemas.android.com/apk/res/android";
	public static final String LOCAL_NS = "http://schemas.polites.com/android";

	private final Semaphore drawLock = new Semaphore(0);
	private Animator animator;

	private Drawable drawable;

	private float x = 0, y = 0;

	private boolean layout = false;

	private float scaleAdjust = 1.0f;
	private float startingScale = 1.0f;

	private float scale = 1.0f;
	private float maxScale = 5.0f;
	private float minScale = 0.75f;
	private float fitScale = 1.0f;
	private float rotation = 0.0f;

	private int lastOrientation = -1;

	private float centerX;
	private float centerY;

	private int hWidth;
	private int hHeight;

	private int resId = -1;
	private boolean recycle = false;
	private boolean strict = false;

	private int displayHeight;
	private int displayWidth;

	private int alpha = 255;
	private ColorFilter colorFilter;

	private int orientation;

	private GestureImageViewListener gestureImageViewListener;
	private GestureImageViewTouchListener gestureImageViewTouchListener;
	
	private OnTouchListener customOnTouchListener;

	public GestureImageView(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
	}

	public GestureImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setImageResource(attrs.getAttributeResourceValue(GLOBAL_NS, "src", -1), true);
		
		String scaleType = attrs.getAttributeValue(GLOBAL_NS, "scaleType");
		
		if(scaleType == null || scaleType.trim().length() == 0) {
			setScaleType(ScaleType.CENTER_INSIDE);
		}
		
		setMinScale(attrs.getAttributeFloatValue(LOCAL_NS, "min-scale", minScale));
		setMaxScale(attrs.getAttributeFloatValue(LOCAL_NS, "max-scale", maxScale));
		setStrict(attrs.getAttributeBooleanValue(LOCAL_NS, "strict", strict));
		setRecycle(attrs.getAttributeBooleanValue(LOCAL_NS, "recycle", recycle));
	}

	public GestureImageView(Context context) {
		super(context);
		setScaleType(ScaleType.CENTER_INSIDE);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		if(drawable != null) {
			int orientation = getResources().getConfiguration().orientation;
			if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
				displayHeight = MeasureSpec.getSize(heightMeasureSpec);

				if(getLayoutParams().width == LayoutParams.WRAP_CONTENT) {
					float ratio = (float) getImageWidth() / (float) getImageHeight();
					displayWidth = Math.round( (float) displayHeight * ratio) ;
				}
				else {
					displayWidth = MeasureSpec.getSize(widthMeasureSpec);
				}
			}
			else {
				displayWidth = MeasureSpec.getSize(widthMeasureSpec);
				if(getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
					float ratio = (float) getImageHeight() / (float) getImageWidth();
					displayHeight = Math.round( (float) displayWidth * ratio) ;
				}
				else {
					displayHeight = MeasureSpec.getSize(heightMeasureSpec);
				}				
			}
		}
		else {
			displayHeight = MeasureSpec.getSize(heightMeasureSpec);
			displayWidth = MeasureSpec.getSize(widthMeasureSpec);
		}

		setMeasuredDimension(displayWidth, displayHeight);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if(changed || !layout) {
			setupCanvas(displayWidth, displayHeight, getResources().getConfiguration().orientation);
		}
	}

	protected void setupCanvas(int measuredWidth, int measuredHeight, int orientation) {

		if(lastOrientation != orientation) {
			layout = false;
			lastOrientation = orientation;
		}

		if(drawable != null && !layout) {
			int imageWidth = getImageWidth();
			int imageHeight = getImageHeight();

			hWidth = Math.round(((float)imageWidth / 2.0f));
			hHeight = Math.round(((float)imageHeight / 2.0f));

			computeCropScale(imageWidth, imageHeight, measuredWidth, measuredHeight);
			computeStartingScale(imageWidth, imageHeight, measuredWidth, measuredHeight);

			scaleAdjust = startingScale;

			this.centerX = (float)measuredWidth / 2.0f;
			this.centerY = (float)measuredHeight / 2.0f;

			x = centerX;
			y = centerY;

			gestureImageViewTouchListener = new GestureImageViewTouchListener(this, measuredWidth, measuredHeight);
			gestureImageViewTouchListener.setMinScale(minScale * fitScale);
			gestureImageViewTouchListener.setMaxScale(maxScale * startingScale);
			gestureImageViewTouchListener.setFitScale(fitScale);

			drawable.setBounds(-hWidth,-hHeight,hWidth,hHeight);

			super.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(customOnTouchListener != null) {
						customOnTouchListener.onTouch(v, event);
					}
					return gestureImageViewTouchListener.onTouch(v, event);
				}
			});	

			layout = true;
		}
	}
	
	protected void computeCropScale(int imageWidth, int imageHeight, int measuredWidth, int measuredHeight) {
		fitScale = Math.min((float) measuredHeight / (float) imageHeight, (float) measuredWidth/ (float) imageWidth);
	}
	
	protected void computeStartingScale(int imageWidth, int imageHeight, int measuredWidth, int measuredHeight) {
		
		measuredWidth -= (getPaddingLeft() + getPaddingRight());
		measuredHeight -= (getPaddingTop() + getPaddingBottom());
		
		switch(getScaleType()) {
			case CENTER: 
				// Center the image in the view, but perform no scaling. 
				startingScale = 1.0f;
				break;
				
			case CENTER_CROP: 
				startingScale = Math.max((float) measuredHeight / (float) imageHeight, (float) measuredWidth/ (float) imageWidth);
				break;
				
			case CENTER_INSIDE: 
				// Scale the image uniformly (maintain the image's aspect ratio) so that both dimensions (width and height) of the image will be equal to or less than the corresponding dimension of the view (minus padding). 
				startingScale = fitScale;
				break;
		}
	}

	protected boolean isRecycled() {
		if(drawable != null && drawable instanceof BitmapDrawable) {
			Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
			if(bitmap != null) {
				return bitmap.isRecycled();
			}
		}
		return false;
	}

	protected void recycle() {
		if(recycle && drawable != null && drawable instanceof BitmapDrawable) {
			Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
			if(bitmap != null) {
				bitmap.recycle();
			}
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if(layout) {
			if(drawable != null && !isRecycled()) {
				canvas.save();
				
				float adjustedScale = scale * scaleAdjust;

				canvas.translate(x, y);

				if(rotation != 0.0f) {
					canvas.rotate(rotation);
				}

				if(adjustedScale != 1.0f) {
					canvas.scale(adjustedScale, adjustedScale);
				}

				drawable.draw(canvas);

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

		if(resId >= 0 && drawable == null) {
			setImageResource(resId);
		}

		super.onAttachedToWindow();
	}

	public void animationStart(Animation animation) {
		if(animator != null) {
			animator.play(animation);
		}
	}

	public void animationStop() {
		if(animator != null) {
			animator.cancel();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		if(animator != null) {
			animator.finish();
		}
		if(recycle && drawable != null && !isRecycled()) {
			recycle();
			drawable = null;
		}
		super.onDetachedFromWindow();
	}

	protected void initImage(boolean original) {
		if(this.drawable != null) {
			this.drawable.setAlpha(alpha);
			this.drawable.setFilterBitmap(true);
			if(colorFilter != null) {
				this.drawable.setColorFilter(colorFilter);
			}
		}
		
		if(!original) {
			layout = false;
			requestLayout();
			redraw();
		}
	}

	public void setImageBitmap(Bitmap image) {
		setImageBitmap(image, false);
	}

	protected void setImageBitmap(Bitmap image, boolean original) {
		this.drawable = new BitmapDrawable(getResources(), image);
		initImage(original);
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		this.drawable = drawable;
		initImage(false);
	}

	public void setImageResource(int id) {
		setImageResource(id, false);
	}

	protected void setImageResource(int id, boolean original) {
		if(this.drawable != null) {
			this.recycle();
		}
		if(id >= 0) {
			this.recycle = true;
			this.resId = id;
			setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), id), original);
		}
	}

	public int getImageWidth() {
		if(drawable != null) {
			return drawable.getIntrinsicWidth();
		}
		return 0;
	}

	public int getImageHeight() {
		if(drawable != null) {
			return drawable.getIntrinsicHeight();
		}
		return 0;
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
			gestureImageViewTouchListener.setMinScale(min * fitScale);
		}
	}

	public void setMaxScale(float max) {
		this.maxScale = max;
		if(gestureImageViewTouchListener != null) {
			gestureImageViewTouchListener.setMaxScale(max * startingScale);
		}
	}

	public void setScale(float scale) {
		scaleAdjust = scale;
	}

	public float getScale() {
		return scaleAdjust;
	}

	public float getImageX() {
		return x;
	}

	public float getImageY() {
		return y;
	}

	public boolean isStrict() {
		return strict;
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	public boolean isRecycle() {
		return recycle;
	}

	public void setRecycle(boolean recycle) {
		this.recycle = recycle;
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

	@Override
	public Drawable getDrawable() {
		return drawable;
	}

	@Override
	public void setAlpha(int alpha) {
		this.alpha = alpha;
		if(drawable != null) {
			drawable.setAlpha(alpha);
		}
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		this.colorFilter = cf;
		if(drawable != null) {
			drawable.setColorFilter(cf);
		}
	}

	@Override
	public void setImageURI(Uri mUri) {
		if ("content".equals(mUri.getScheme())) {
			try {
				String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
				
				Cursor cur = getContext().getContentResolver().query(mUri, orientationColumn, null, null, null);
				
				if (cur != null && cur.moveToFirst()) {
					orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
				}  
				
				InputStream in = null;
				
				try {
					in = getContext().getContentResolver().openInputStream(mUri);
					Bitmap bmp = BitmapFactory.decodeStream(in);
					
					if(orientation != 0) {
						Matrix m = new Matrix();
						m.postRotate(orientation);
						Bitmap rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
						bmp.recycle();
						setImageDrawable(new BitmapDrawable(getResources(), rotated));
					}
					else {
						setImageDrawable(new BitmapDrawable(getResources(), bmp));
					}
				}
				finally {
					if(in != null) {
						in.close();
					}
					
					if(cur != null) {
						cur.close();
					}
				}
			}
			catch (Exception e) {
				Log.w("GestureImageView", "Unable to open content: " + mUri, e);
			}
		}
		else {
			setImageDrawable(Drawable.createFromPath(mUri.toString()));
		}

		if (drawable == null) {
			Log.e("GestureImageView", "resolveUri failed on bad bitmap uri: " + mUri);
			// Don't try again.
			mUri = null;
		}
	}

	@Override
	public Matrix getImageMatrix() {
		if(strict) {
			throw new UnsupportedOperationException("Not supported");
		}		
		return super.getImageMatrix();
	}

	@Override
	public void setScaleType(ScaleType scaleType) {
		if(scaleType == ScaleType.CENTER ||
			scaleType == ScaleType.CENTER_CROP ||
			scaleType == ScaleType.CENTER_INSIDE) {
			
			super.setScaleType(scaleType);
		}
		else if(strict) {
			throw new UnsupportedOperationException("Not supported");
		}
	}

	@Override
	public void invalidateDrawable(Drawable dr) {
		if(strict) {
			throw new UnsupportedOperationException("Not supported");
		}
		super.invalidateDrawable(dr);
	}

	@Override
	public int[] onCreateDrawableState(int extraSpace) {
		if(strict) {
			throw new UnsupportedOperationException("Not supported");
		}
		return super.onCreateDrawableState(extraSpace);
	}

	@Override
	public void setAdjustViewBounds(boolean adjustViewBounds) {
		if(strict) {
			throw new UnsupportedOperationException("Not supported");
		}
		super.setAdjustViewBounds(adjustViewBounds);
	}

	@Override
	public void setImageLevel(int level) {
		if(strict) {
			throw new UnsupportedOperationException("Not supported");
		}
		super.setImageLevel(level);
	}

	@Override
	public void setImageMatrix(Matrix matrix) {
		if(strict) {
			throw new UnsupportedOperationException("Not supported");
		}
	}

	@Override
	public void setImageState(int[] state, boolean merge) {
		if(strict) {
			throw new UnsupportedOperationException("Not supported");
		}
	}

	@Override
	public void setSelected(boolean selected) {
		if(strict) {
			throw new UnsupportedOperationException("Not supported");
		}
		super.setSelected(selected);
	}

	@Override
	public void setOnTouchListener(OnTouchListener l) {
		this.customOnTouchListener = l;
	}
}
