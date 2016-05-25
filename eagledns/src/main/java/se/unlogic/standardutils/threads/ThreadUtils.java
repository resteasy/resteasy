package se.unlogic.standardutils.threads;


public class ThreadUtils {
	
	public static void run(Runnable runnable, String threadName, boolean daemon){
		
		Thread thread = new Thread(runnable, threadName);
		
		thread.setName(threadName);
		
		thread.setDaemon(daemon);
		
		thread.start();
	}
	
	public void sleep(long millis){
		
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {}
	}
}
