package com.polites.android.example;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import com.polites.android.GestureImageView;

public class StandardImageProgrammatic extends ExampleActivity {
	
	protected GestureImageView view;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.empty);
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        view = new GestureImageView(this);
        view.setImageResource(R.drawable.image);
        view.setLayoutParams(params);
        
        ViewGroup layout = (ViewGroup) findViewById(R.id.layout);

        layout.addView(view);
    }
}