/**
 * 
 */
package com.polites.android;

/**
 * @author jasonpolites
 *
 */
public abstract class GestureImageViewListener {

	public void onTouch(float x, float y) { }
	
	public void onScale(float scale) { }
	
	public void onPosition(float x, float y) { }
	
	public void onTouchOutsideDrawable(float x, float y) { }
	
}
