package edu.upenn.cis.cis555.webserver;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class HttpRequest {
	String request_line = null; 
	String method = null; 
	String file_path = null;  
	String version = null;
	HashMap<String, String> headers;
	String request_body = null; //message body with POST
	String query = null; // params with GET 
	HashMap<String, String> params; //params in request body with POST

	boolean isNormal = true;
	Scanner in; 
	
	public HttpRequest(Scanner input) {
		this.in = input; 
		headers = new HashMap<>(); 
		params = new HashMap<>(); 
		parseRequestLine();
		parseHeaders(); 
		if (method.equals("POST") && headers.containsKey("content-length")) {
			parseRequestBody(); 
		}
	}

	// print parts of the request
	public void printRequest() {
//		System.out.println("Request line: " + request_line);
		System.out.println("Headers: ");
		for (String key: headers.keySet()) {
			System.out.print(key + " : ");
			System.out.println(headers.get(key));
		}
//		
		System.out.println("Message: " + request_body);
//		for (String key: params.keySet()) {
//			System.out.print(key + " : "); 
//			System.out.println(params.get(key));
//		}
//		System.out.println();
		System.out.println("filepath: " + getFilePath());
	}
	
	// read in request line
	public void parseRequestLine() {
		if (in.hasNextLine()) {
			request_line = in.nextLine(); 
			System.out.println(request_line);
		} else {
			isNormal = false;
			return; 
		}
		String req_items[] = request_line.split("\\s+"); //matches white spaces
		//System.out.println("Resource_path: " + req_items[1]);
		
		if (req_items.length != 3) {
			isNormal = false; 
		}
		// verify method 
		method = req_items[0]; 
		if (!(method.equals("GET") || method.equals("HEAD") || method.equals("POST"))) {
			System.out.println("Bad method");
			isNormal = false; 
		}
		
		// get file path
		String uri = req_items[1];
		if (uri.indexOf('?') != -1) {
			String[] uri_items = uri.split("\\?");
			file_path = uri_items[0]; 
			query = uri_items[1];
			constructParameters(query);
		} else {
			file_path = decode(uri); 
		}
		
		// verify HTTP version 
		version = req_items[2]; 
		if (!version.matches("HTTP/1\\.[01]")) {
			isNormal = false; 
		}
	}
	
	// read in headers 
	public void parseHeaders() {
		String line = null; 
		while (in.hasNextLine() && !(line=in.nextLine()).isEmpty()) {
			String[] line_items = line.split(":", 2);
			headers.put(line_items[0].trim().toLowerCase(), line_items[1].trim()); 
		}
		
		if (version.equals("HTTP/1.1") && !headers.containsKey("host")) {
			isNormal = false; 
		}
	}
	
	// read in request message body 
	public void parseRequestBody() {
		int contentLen = Integer.parseInt(headers.get("content-length"));
		StringBuilder body = new StringBuilder();
		in.useDelimiter(""); // read characters one by one
		for (int i=0; i<contentLen; i++) {
			body.append(in.next());
		}
		request_body = body.toString(); 
		// form submission
		if (getContentType().equals("application/x-www-form-urlencoded")){
			constructParameters(request_body);
		}
	}
	
	public void constructParameters (String str) {
		String[] param_items = str.split("&");
		if (param_items.length > 0) {
			for (String item: param_items) {
				String[] pairs = item.split("=");
				if (pairs.length > 0) {
					params.put(decode(pairs[0]), decode(pairs[1]));
				} else {
					// TODO error message: not enough parameters 
				}
			}
		} else {
			// TODO Error message
		}
	}
	
	// Decode URLs 
	public String decode(String str) {
		String rst = null; 
		try {
			rst = URLDecoder.decode(str, "UTF-8");
		} catch (UnsupportedEncodingException enc) {
			enc.printStackTrace();
		}
		return rst; 
	}
	
	public boolean servletMatch() {
		// TODO Check if file path matches a servlet path mapping
		return false; 
	}
	
	public String getFilePath() {
		return file_path;
	}
	
	public String getMethod() {
		return method;  
	}
	
	public String getVersion() {
		return version; 
	}
	
	public boolean isNormal() {
		return isNormal; 
	}
	
	public HashMap<String, String> getHeaders() {
		return headers; 
	}
	
	public String getReqBody() {
		return request_body;
	}
	
	public String getContentType() {
		if (headers.containsKey("content-type")) {
			return headers.get("content-type"); 
		} 
		return null; 
	}
	
	
	public HashMap<String, String> getParams() {
		return params; 
	}
}
