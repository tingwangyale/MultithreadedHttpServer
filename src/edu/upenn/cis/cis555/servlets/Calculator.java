package edu.upenn.cis.cis555.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Calculator extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/html");
		try {
			PrintWriter out = response.getWriter();
			int val1 = Integer.parseInt(request.getParameter("left"));
			int val2 = Integer.parseInt(request.getParameter("right"));
			String sum = String.valueOf(val1 + val2); 
			
			out.println("<html><body><p>");
			out.println(sum);
			out.println("</p></body></html>");
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/html");
		try {
			PrintWriter out = response.getWriter();
//			int val1 = Integer.parseInt(request.getParameter("left"));
//			int val2 = Integer.parseInt(request.getParameter("right"));
//			String sum = String.valueOf(val1 + val2);
			
			String val1 = request.getParameter("left");
			String val2 = request.getParameter("right");
			String sum = val1.concat(val2);
			out.println("<html><body><p>");
			out.println(sum);
			out.println("</p></body></html>");
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public void service(HttpServletRequest request, HttpServletResponse response) {
		if (request.getMethod().equals("POST")) {
			doPost(request, response);
		} else if (request.getMethod().equals("GET")) {
			doGet(request, response);
		}
	}
	
}
