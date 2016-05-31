package edu.upenn.cis.cis555.webserver;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpWorker reads request and generates response
 */

public class HttpTask implements Runnable {
	Socket socket; //reference to the connection socket 
	String root_path; 
	String servletClassName; 
	HttpRequest request; 
	int taskId; 
	
	// constructor
	public HttpTask (Socket s, String root_dir, int id) {
		this.socket = s; 
		this.root_path = root_dir;  
		this.taskId = id; 
	}
	
	@Override
	public void run() {
		
		// utility block for testing multithreading
		for (int i=0; i<5; i++) {
			long threadId = Thread.currentThread().getId(); 
			System.out.println("Processing request " + taskId + " in Thread " + threadId);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Scanner in = null; 
		PrintWriter out= null; 
		
		try {
			in = new Scanner(socket.getInputStream());
			out = new PrintWriter(socket.getOutputStream(), true);
		
			request = new HttpRequest(in);
			//request.printRequest();
			
			HttpResponse response = new HttpResponse(out, request, root_path);
			
			if (request.isNormal()) {
				// check if match servlet class 
				if ((servletClassName = WebXmlParser.servletClassName(request.getFilePath()))!= null) {			
					HttpServletRequest servletReq = new NewHttpServletRequest(request);
					HttpServletResponse servletRes = new NewHttpServletResponse(out); 
					
					//System.out.println("class name: " + servletClassName);
					
					HttpServlet servlet = createServlet(); 
					servlet.service(servletReq, servletRes);
					
				} else {
					response.generateResponse();
				}
			} else {
				response.generateResponse(); 
			}
			
		} catch (IOException io) {
			System.out.println("Connection error.");
		} catch (ServletException s) {
			s.printStackTrace();
		} catch (ClassNotFoundException classnotfound) {
			System.out.println("Servlet class not found");
			classnotfound.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close(); 
				out.close(); 
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
	
	public HttpServlet createServlet() throws Exception {
		Class servletClass = Class.forName(servletClassName); 
		return (HttpServlet) servletClass.newInstance(); 
	}
	
	// utility method, mainly for testing
	public String getReq() {
		return request.getMethod() + " " + request.getFilePath() + " HTTP/1.1";
	}
}
