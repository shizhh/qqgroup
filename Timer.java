package com.selenium.qqgroup;

public class Timer extends Thread {

	static long time = 3000;
	@Override
	public void run() {
		try {
			sleep(time);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			qqgroup.timeout();
		}
	}
}
