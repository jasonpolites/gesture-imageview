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
