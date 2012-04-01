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
