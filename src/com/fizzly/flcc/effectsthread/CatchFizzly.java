package com.fizzly.flcc.effectsthread;

import java.util.Random;

import com.fizzly.flcc.device.FizzlyDevice;

public class CatchFizzly extends Thread{
	
	private int millis = 100;
	private FizzlyDevice mFizzlyDevice = null;
	
	public CatchFizzly (FizzlyDevice mFizzlyDevice, int millis){
		this.millis = millis;
		this.mFizzlyDevice = mFizzlyDevice;
	}

	@Override
	public void run() {
		super.run();

		Random r = new Random();
		
    	mFizzlyDevice.setRgbColor(100, 100, 0, millis);
    	try {
			Thread.sleep(r.nextInt(1000 - 500 + 1) + 500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

    	mFizzlyDevice.setRgbColor(100, 0, 100, millis);
    	try {
			Thread.sleep(r.nextInt(1000 - 500 + 1) + 500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

    	mFizzlyDevice.setRgbColor(100, 0, 0, millis);
    	try {
			Thread.sleep(r.nextInt(1000 - 500 + 1) + 500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

    	mFizzlyDevice.setRgbColor(0, 100, 0, millis);
    	try {
			Thread.sleep(r.nextInt(1000 - 500 + 1) + 500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	mFizzlyDevice.setRgbColor(0, 100, 100, millis);
    	try {
			Thread.sleep(r.nextInt(1000 - 500 + 1) + 500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    		
    	
	}
	
}
