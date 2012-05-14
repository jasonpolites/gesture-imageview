package com.polites.android.example;

import android.os.Bundle;

public class StandardImageProgrammaticWithStartSettings extends StandardImageProgrammatic {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	view.setStartingScale(2.0f);
    	view.setStartingPosition((float)view.getImageWidth() / 2.0f, (float)view.getImageHeight() / 2.0f);
    }
}