package com.polites.android.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import com.polites.android.GestureImageView;

public class StandardImageProgrammatic extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.empty);
    	
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        
        GestureImageView view = new GestureImageView(this);
        view.setImageResource(R.drawable.image);
        view.setLayoutParams(params);
        
        ViewGroup layout = (ViewGroup) findViewById(R.id.layout);

        layout.addView(view);
    }
}