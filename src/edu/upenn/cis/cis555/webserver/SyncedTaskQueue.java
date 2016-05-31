package edu.upenn.cis.cis555.webserver;

import java.util.LinkedList;

/**
 * A blocking queue of HttpTasks 
 */

public class SyncedTaskQueue {
	
	LinkedList<Runnable> taskQueue; 
	int limit;
	
	public SyncedTaskQueue(int limit) {
		this.limit = limit;
		taskQueue = new LinkedList<>(); 
	}
	
	public synchronized void enqueue(Runnable task) {
		while (taskQueue.size() == this.limit) {
			try {
				wait();	 // Relinquish lock and wait
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 		
		}
		if (taskQueue.size() == 0) {
			notifyAll(); 
		}
		taskQueue.add(task);
	}
	
	
	public synchronized Runnable dequeue() {
		while (taskQueue.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}
		
		// if the queue was full when calling dequeue
		if (taskQueue.size() == this.limit) { 
			notifyAll(); 
		} 
		
		Runnable item = taskQueue.remove(0);
		return item;
	}
	
	
	public synchronized int size() {
		return taskQueue.size(); 
	}
}