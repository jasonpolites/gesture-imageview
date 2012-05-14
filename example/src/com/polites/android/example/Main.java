/*
 * Copyright (c) 2012 Jason Polites
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.polites.android.example;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @author Jason Polites
 * 
 */
public class Main extends ListActivity {

	static final String[] listItems = { 
		"Single Image XML Layout", 
		"Single Image Programmatic", 
		"Single Image Programmatic with onClick event", 
		"Single Image Programmatic with start scale & position", 
		"Single Image XML Layout with start scale & position", 
		"Double Image (With delayed load)", 
		"ScaleType CENTER Large", 
		"ScaleType CENTER_CROP Large", 
		"ScaleType CENTER_INSIDE Large", 
		"ScaleType CENTER Small", 
		"ScaleType CENTER_CROP Small", 
		"ScaleType CENTER_INSIDE Small",  
		"ScaleType CENTER Square", 
		"ScaleType CENTER_CROP Square", 
		"ScaleType CENTER_INSIDE Square",  
		"ScaleType CENTER Portrait", 
		"ScaleType CENTER_CROP Portrait", 
		"ScaleType CENTER_INSIDE Portrait",  		
	};
	
	static final Class<?>[] actvities = { 
		StandardImageXML.class,
		StandardImageProgrammatic.class,
		StandardImageProgrammaticWithOnClick.class,
		StandardImageProgrammaticWithStartSettings.class,
		StandardImageXMLWithStartSettings.class,
		DoubleImage.class,
		ScaleTypeCenter.class,
		ScaleTypeCenterCrop.class,
		ScaleTypeCenterInside.class,
		ScaleTypeCenterSmall.class,
		ScaleTypeCenterCropSmall.class,
		ScaleTypeCenterInsideSmall.class,
		ScaleTypeCenterSquare.class,
		ScaleTypeCenterCropSquare.class,
		ScaleTypeCenterInsideSquare.class,
		ScaleTypeCenterPortrait.class,
		ScaleTypeCenterCropPortrait.class,
		ScaleTypeCenterInsidePortrait.class};	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, actvities[position]);
		startActivity(intent);
	}
}
