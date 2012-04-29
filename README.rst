GestureImageView
================

This is a simple Android View class which provides basic pinch and zoom capability for images.

Can be used as a replacement for a standard ImageView when you want to include pinch and zoom.

Features:
~~~~~~~~~
1. Pinch zoom in place (i.e. zoom occurs from point of touch)
2. Panning with fling gesture
3. Double tap reset
4. Configurable zoom boundaries (min/max)

Limitations:
~~~~~~~~~~~~
1. Does not support Rotation
2. Does not support Pan and Zoom together
3. Only supports Bitmap objects and image resources (i.e. does not support setting a Drawable that is NOT a bitmap/png/jpg)
4. Not all methods of ImageView class are supported (will throw UnsupportedOperationException if strict is true)

What's New!
~~~~~~~~~~~

- Added support for CENTER, CENTER_INSIDE and CENTER_CROP scale types
- Added support for custom OnTouchListener on GestureImageView
- Fixed NPE when no drawable set on view
- Fixed/improved calculation of scale limits

Usage
~~~~~

Notes:
------
1. Setting gesture-image:strict to true will result in UnsupportedOperationException if an unsupported method is called.
2. Setting gesture-image:recycle to true will automatically reycle bitmap images when the view is destroyed.

Configured as View in layout.xml
--------------------------------
code::

	<LinearLayout 
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:gesture-image="http://schemas.polites.com/android"
	    android:layout_width="fill_parent"
	    android:layout_height="fill_parent">

	    <com.polites.android.GestureImageView
	        android:id="@+id/image"
	        android:layout_width="fill_parent"
	    	android:layout_height="wrap_content" 
	    	android:src="@drawable/image"
	    	gesture-image:min-scale="0.1"
	    	gesture-image:max-scale="10.0"
	    	gesture-image:strict="false"
	    	gesture-image:recycle="true"/>
	    	
	</LinearLayout>
    	
Configured Programmatically
---------------------------
code::    	

	import com.polites.android.GestureImageView;
	
	import android.app.Activity;
	import android.os.Bundle;
	import android.view.ViewGroup;
	import android.widget.LinearLayout.LayoutParams;
	
	public class SampleActivity extends Activity {
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.main);
	        
	        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	        
	        GestureImageView view = new GestureImageView(this);
	        view.setImageResource(R.drawable.image);
	        view.setLayoutParams(params);
	        
	        ViewGroup layout = (ViewGroup) findViewById(R.id.layout);
	
	        layout.addView(view);
	    }
	}
	
License
~~~~~~~
`Apache License 2.0 <http://www.apache.org/licenses/LICENSE-2.0>`_