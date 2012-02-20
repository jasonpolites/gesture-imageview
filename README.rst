GestureImageView
================

This is a simple Android View class which provides basic pinch-zoom capability for images.

Features:
~~~~~~~~~
1. Pinch zoom in place (i.e. zoom occurs from point of touch)
2. Panning
3. Double tap reset
4. Configurable zoom boundaries (min/max)

Limitations:
~~~~~~~~~~~~
1. Does not support Fling
2. Does not support Rotation
3. Does not support Pan and Zoom together
4. Only supports Bitmap objects and image resources (i.e. does not support setting a Drawable that is not a bitmap/png/jpg)
5. Does not support multiple images on screen

Usage
-----

Configured as View in layout.xml
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
code::

    <com.polites.android.GestureImageView
        android:id="@+id/image"
        android:layout_width="fill_parent"
    	android:layout_height="wrap_content" 
    	gesture-image:min-scale="0.1"
    	gesture-image:max-scale="10.0"
    	android:src="@drawable/image"/>
    	
    	
Configured Programmatically
~~~~~~~~~~~~~~~~~~~~~~~~~~~
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
	