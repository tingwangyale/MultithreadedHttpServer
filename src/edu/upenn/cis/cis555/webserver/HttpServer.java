package edu.upenn.cis.cis555.webserver;
import java.io.*;
import java.net.*; 

public class HttpServer {
	static int port; 
	static String root_dir; 
	static String xml_path; 
	
	public static void main(String args[]) throws Exception {
		//int port = Integer.parseInt(args[0]);
		//String root_dir = args[1]; 
		port = 8080; 
		root_dir = "C:/Users/Ting/Desktop";  
		xml_path = "C:/Users/Ting/Google Drive/MCIT program/CIS555/workspace/HW1/WEB-INF/web.xml";
		
		ServerSocket serverSocket = new ServerSocket(port);
		
		// TODO parse web.xml
		WebXmlParser parser = new WebXmlParser(xml_path);
		
		while (true) {
			// Listen for a TCP connection request 
			Socket clientSocket = serverSocket.accept();
			System.out.println("Accepted connection: " + clientSocket);
		
			HttpWorker worker = new HttpWorker(clientSocket, root_dir);
			Thread thread = new Thread(worker);  
			thread.start(); 
		}
	}
}
