package com.polites.android.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class StandardImageXML extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.standard_image);
    }
}