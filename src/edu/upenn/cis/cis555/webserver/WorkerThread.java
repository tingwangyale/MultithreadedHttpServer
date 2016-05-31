package edu.upenn.cis.cis555.webserver;

public class WorkerThread extends Thread {
	
	SyncedTaskQueue taskQueue; 
	private boolean isStopped = false; 
	private boolean isShutdown = false; 
	public int threadId;
	
	//constructor 
	public WorkerThread(SyncedTaskQueue taskQueue, int num) {
		this.taskQueue = taskQueue;
		this.isStopped = false;
		this.threadId = num; 
	}
	
	// continuously dequeue from the blocking queue and execute run() in HttpTask
	public void run() {
		while (!isShutdown()) {
			long threadId = Thread.currentThread().getId(); 
			System.out.println("Thread " + threadId + " is trying to dequeue from queue of size " + taskQueue.size());
			Runnable task = taskQueue.dequeue();
			task.run(); 
		}
	}
		
	public synchronized boolean isStopped() {
		return isStopped; 
	}
	
	public synchronized void doStop() {
		isStopped = true; 
		this.interrupt(); 
	}
	
	public synchronized void shutdown() {
		isShutdown = true; 
		interrupt(); 
	}
	
	public synchronized boolean isShutdown() {
		return isShutdown; 
	}
}