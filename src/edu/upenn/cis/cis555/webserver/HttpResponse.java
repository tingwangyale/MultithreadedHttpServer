package edu.upenn.cis.cis555.webserver;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class HttpResponse {
	final static String CRLF = "\r\n"; // for convenience 
	HttpRequest request; 
	String root_path; 
	PrintWriter out; 
	
	StringBuilder response; 
	StringBuilder statusLine; 
	StringBuilder responseHeaders; 
	StringBuilder entityBody; 
	
	String status = " 200 OK";
	String contentType = "Content-Type: "; 
	String contentLength = "Content-Length: ";
	String server = "Server: Apache";
	String lastModified = "Last Modified: ";
	String date = "Date: ";
	
	public HttpResponse(PrintWriter out, HttpRequest request, String root) {
		this.out = out; 
		this.request = request; 
		this.root_path = root; 
		response = new StringBuilder(); 
		statusLine = new StringBuilder(); 
		responseHeaders = new StringBuilder(); 
		entityBody = new StringBuilder(); 
	}
	
	public void generateResponse() {
		date += refFormatNowDate(); 
		if (request.getMethod().equals("GET")) {			
			generateGetResponse(); 
		} else if (request.getMethod().equals("HEAD")) {
			generateHeadResponse(); 
		} else if (request.getMethod().equals("POST")) {
			generatePostResponse(); 
		}
		out.println(response.toString());
	}
	
	public void generateGetResponse() {
		try {
			String file_path = URLDecoder.decode(request.getFilePath(), "UTF-8");
			File root = new File(root_path);
			File requested_file = new File(root, file_path).getCanonicalFile();
			if (!requested_file.exists()) {
				status = "404 Not Found";
			} else if (requested_file.isDirectory()) {
				String[] files = requested_file.list();
				entityBody.append("<html><body>");
				for (String f: files) {
					entityBody.append("<p>"); 
					entityBody.append(f);
					entityBody.append("</p>");
				}
				entityBody.append("</body></html>");
				contentType += " text/html"; 
			} else {
				Scanner in = new Scanner(requested_file);
				while (in.hasNextLine()) {
					entityBody.append(in.nextLine());
				}
				contentType += fileType(request.getFilePath()); 
			}
		} catch (UnsupportedEncodingException e) {
			//TODO throw 500 Internal Server Error
			status = "500 Internal Server Error";
			System.out.println(e);
		} catch (IOException io) {
			status = "500 Internal Server Error";
			System.out.println(io);
		} 
		
		buildStatusLine(); 
		buildHeaders(); 
		response.append(statusLine); 
		response.append(responseHeaders);
		response.append(entityBody.toString()); 
	}
	
	public StringBuilder generateHeadResponse() {
		buildStatusLine(); 
		buildHeaders(); 
		response.append(statusLine); 
		response.append(responseHeaders);
		return response; 
	}
	
	public void generatePostResponse() {
		// TODO Post request response if servlet match not found
	}
	
	public void buildStatusLine() {
		statusLine.append(request.getVersion()); 
		statusLine.append(status); 
		statusLine.append(CRLF);
	}
	
	public void buildHeaders() {
		responseHeaders.append(date); responseHeaders.append(CRLF);
		responseHeaders.append(server); responseHeaders.append(CRLF);
		responseHeaders.append(lastModified); responseHeaders.append(CRLF);	
		responseHeaders.append(contentType);  responseHeaders.append(CRLF);
		responseHeaders.append(contentLength);responseHeaders.append(CRLF);
		responseHeaders.append("\n");
	}	
	
	public String refFormatNowDate() {
		  Date nowTime = new Date(System.currentTimeMillis());
		  SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd");
		  String retStrFormatNowDate = sdFormatter.format(nowTime);
		  return retStrFormatNowDate;
	}
	
	public String fileType(String filename) {
		if (filename.endsWith(".htm") || filename.endsWith(".html")) {
			return "text/html"; 
		} else if (filename.endsWith(".jpg")) {
			return "image/jpeg"; 
		} else if (filename.endsWith(".gif")) {
			return "image/gif"; 
		} else if (filename.endsWith(".png")) {
			return "image/png";
		} else if (filename.endsWith(".txt")) {
			return "text/plain"; 
		}
		return "application/"; 
	}
}
