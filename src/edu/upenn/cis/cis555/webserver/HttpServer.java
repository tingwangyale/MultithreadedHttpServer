package edu.upenn.cis.cis555.webserver;

import java.net.*; 

public class HttpServer {
	static int port; 
	static String root_dir; 
	static String xml_path; 
	static final int numOfThreads = 2; //number of worker threads 
	static final int queueSize = 2;  
	
	public static void main(String args[]) throws Exception {
		// TODO parse args  
		port = 8080; 
		root_dir = "C:/Users/Ting/Desktop";  
		xml_path = "C:/Users/Ting/Google Drive/MCIT program/CIS555/workspace/HW1/WEB-INF/web.xml";
		
		ServerSocket serverSocket = new ServerSocket(port);
		WebXmlParser parser = new WebXmlParser(xml_path);
		
		// construct thread pool; initiate task consumers 
		ThreadPool pool = new ThreadPool(numOfThreads, queueSize);
		int requestCount = 0; 
		while (true) {
			// Listen for a TCP connection request 
			Socket clientSocket = serverSocket.accept();
			System.out.println("Accepted connection: " + clientSocket);
			requestCount++; 
			pool.execute(new HttpTask(clientSocket, root_dir, requestCount)); 
		}
	}
}
