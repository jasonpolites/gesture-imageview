GestureImageView
================

This is a simple Android View class which provides basic pinch-zoom capability for images.

Features:

1. Pinch zoom in place (i.e. zoom occurs from point of touch)
2. Panning
3. Double tap reset
4. Configurable zoom boundaries (min/max)

NOT Implemented (and may never be :/):

1. Fling
2. Rotation
3. Pan and Zoom together

Usage
-----

Configured as View in layout.xml
================================
code::

    <com.polites.android.GestureImageView
        android:id="@+id/image"
        android:layout_width="fill_parent"
    	android:layout_height="wrap_content" 
    	gesture-image:min-scale="0.1"
    	gesture-image:max-scale="10.0"
    	android:src="@drawable/image"/>
