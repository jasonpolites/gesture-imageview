package com.polites.android.example;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class StandardImageProgrammaticWithOnClick extends StandardImageProgrammatic {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	view.setClickable(true);
    	view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(v.getContext()).setMessage("test").create().show();
			}
		});
    }
}