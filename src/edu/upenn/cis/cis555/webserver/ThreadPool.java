package edu.upenn.cis.cis555.webserver;
import java.util.ArrayList;

public class ThreadPool {
	int numOfThreads; 
	boolean isStopped = false; 
	ArrayList<WorkerThread> threads; 
	SyncedTaskQueue taskQueue; // the shared blocking queue 
	
	//constructor 
	public ThreadPool(int numOfThreads, int size) {
		this.numOfThreads = numOfThreads; 
		this.taskQueue = new SyncedTaskQueue(size);
		threads = new ArrayList<>(); 
		
		/* create and start worker threads waiting to process tasks in the queue */
		for (int i=0; i<numOfThreads; i++) {
			WorkerThread thread = new WorkerThread(taskQueue, i);
			threads.add(thread);
			thread.start();
		}
	}
	
	// add a task to the shared task queue every time a request arrives 
	public synchronized void execute(Runnable task) {
		taskQueue.enqueue(task);
	}
	
	public synchronized void stop(){
        this.isStopped = true;
        for(WorkerThread thread : threads){
           thread.doStop(); 
        }
    }
	
}
