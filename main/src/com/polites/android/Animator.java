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
package com.polites.android;


/**
 * @author Jason Polites
 *
 */
public class Animator extends Thread {
	
	private GestureImageView view;
	private Animation animation;
	private boolean running = false;
	private boolean active = false;
	private long lastTime = -1L;

	public Animator(GestureImageView view, String threadName) {
		super(threadName);
		this.view = view;
	}
	
	@Override
	public void run() {
		
		running = true;
		
		while(running) {
				
			while(active && animation != null) {
				long time = System.currentTimeMillis();
				active = animation.update(view, time - lastTime);
				view.redraw();
				lastTime = time;					
				
				while(active) {
					try {
						if(view.waitForDraw(32)) { // 30Htz
							break;
						}
					}
					catch (InterruptedException ignore) {
						active = false;
					}
				}
			}
			
			synchronized(this) {
				if(running) {
					try {
						wait();
					}
					catch (InterruptedException ignore) {}
				}
			}
		}
	}

	public synchronized void finish() {
		running = false;
		active = false;
		notifyAll();
	}

	public void play(Animation transformer) {
		if(active) {
			cancel();
		}
 		this.animation = transformer;
 		
 		activate();
	}
	
	public synchronized void activate() {
		lastTime = System.currentTimeMillis();
		active = true;
		notifyAll();
	}
	
	public void cancel() {
		active = false;
	}
}
