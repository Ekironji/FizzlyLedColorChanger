package com.fizzly.flcc.effectsthread;

import com.fizzly.flcc.device.FizzlyDevice;

public class Rainbow extends Thread{
	
	private int millis = 100;
	private FizzlyDevice mFizzlyDevice = null;
	
	public Rainbow (FizzlyDevice mFizzlyDevice, int millis){
		this.millis = millis;
		this.mFizzlyDevice = mFizzlyDevice;
	}

	@Override
	public void run() {
		super.run();

    	mFizzlyDevice.setRgbColor(100, 0, 0, millis);
    	try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

    	mFizzlyDevice.setRgbColor(0, 0, 100, millis);
    	try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

    	mFizzlyDevice.setRgbColor(0, 100, 0, millis);
    	try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

    	mFizzlyDevice.setRgbColor(100, 0, 0, millis);
    	try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	
	}
	
}
