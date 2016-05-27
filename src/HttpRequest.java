import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class HttpRequest implements Runnable {
	final static String CRLF = "\r\n"; // for convenience 
	Socket socket; //reference to the connection socket 
	String root_path; 
	String request_line = null; 
	HashMap<String, String> headers;
	String request_body = null; 
	
	StringBuilder response; 
	StringBuilder statusLine;
	StringBuilder responseHeaders; 
	StringBuilder entityBody; 
	
	String version = "HTTP/1.1 ";
	String status = "200 OK";
	String contentType = "Content-Type: "; 
	String contentLength = "Content-Length: ";
	String server = "Server: Apache";
	String lastModified = "Last Modified: ";
	String date = "Date: ";
	
	public HttpRequest(Socket s, String root_dir) {
		this.socket = s; 
		this.root_path = root_dir; 
		headers = new HashMap<>(); 
		response = new StringBuilder(); 
		statusLine = new StringBuilder();
		responseHeaders = new StringBuilder(); 
		entityBody = new StringBuilder();
	}
	
	public void run() {
		Scanner in = null; 
		PrintWriter out= null; 
		
		try {
			in = new Scanner(socket.getInputStream());
			out = new PrintWriter(socket.getOutputStream(), true);
					
			// get request and generate response 
			if (in.hasNextLine()) {
				if (isValidRequest(in) && isValidHeaders(in)) { 
					// if there exists message body, parse it
					if (getReqMethod().equals("POST") && headers.containsKey("content-length")) {
						System.out.println("getting request body ...");
						getReqBody(in, headers.get("content-length")); 
					}
					
					printRequest();
					
//					if (mapped to the web-xml servlet) {
//						HttpServletRequest(request);
//					}
					
					String response = generateResponse(getReqMethod()); 
					out.println(response);
				} else {
					out.println("HTTP/1.1 500 Error\n\nNot understood: \"" + request_line + "\"");
				}
			}
			
		} catch (IOException io) {
			System.out.println("Connection error.");
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

	// print parts of the request
	public void printRequest() {
		System.out.println("Request line: " + request_line);
		System.out.println("Headers: ");
		for (String key: headers.keySet()) {
			System.out.print(key + " : ");
			System.out.println(headers.get(key));
		}
		System.out.println();
		System.out.println("Message: " + request_body);
	}
	
	// read in request_line
	public boolean isValidRequest(Scanner in) {
		if (in.hasNextLine()) {
			request_line = in.nextLine(); 
		} else {
			return false; 
		}
		
		String req_items[] = request_line.split("\\s+"); //matches white spaces
		
		System.out.println("Resource_path: " + req_items[1]);
		
		if (req_items.length != 3) {
			return false; 
		}
		// verify method 
		String method = getReqMethod(); 
		if (!(method.equals("GET") || method.equals("HEAD") || method.equals("POST"))) {
			System.out.println("Bad method");
			return false;
		}
		
		// verify HTTP version 
		String version = getReqVersion(); 
		if (!version.matches("HTTP/1\\.[01]")) {
			return false; 
		}
		return true; 
	}
	
	// read in headers 
	public boolean isValidHeaders(Scanner in) {
		String line = null; 
		while (in.hasNextLine() && !(line=in.nextLine()).isEmpty()) {
			String[] line_items = line.split(":", 2);
			headers.put(line_items[0].trim().toLowerCase(), line_items[1].trim()); 
		}
		
		if (!headers.containsKey("host")) {
			return false; 
		}
		return true; 
	}
	
	// read in request message body 
	public void getReqBody(Scanner in, String len) {
		int contentLen = Integer.parseInt(len);
		StringBuilder body = new StringBuilder();
		in.useDelimiter(""); // read characters one by one
		for (int i=0; i<contentLen; i++) {
			body.append(in.next());
		}
		request_body = body.toString(); 
	}
	
	public String refFormatNowDate() {
		  Date nowTime = new Date(System.currentTimeMillis());
		  SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd");
		  String retStrFormatNowDate = sdFormatter.format(nowTime);
		  return retStrFormatNowDate;
	}
	
	public String generateResponse(String method) {
		date += refFormatNowDate(); 
		if (method.equals("GET")) {			
			generateGetResponse(); 
		} else if (method.equals("HEAD")) {
			generateHeadResponse(); 
		} else if (method.equals("POST")) {
			//TODO
		}
		
		return response.toString(); 
	}
	
	public void generateGetResponse() {
		try {
			String resource_path = URLDecoder.decode(getResourcePath(), "UTF-8");
			File root = new File(root_path);
			File requested_file = new File(root, resource_path).getCanonicalFile();
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
				contentType += fileType(getResourcePath()); 
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
		return "application/octet-stream"; 
	}
	
	public String getResourcePath() {
		//Extract the filename/directory path from the request line.
        StringTokenizer tokens = new StringTokenizer(request_line);
        tokens.nextToken();  // skip over the request method 
        String file_path = tokens.nextToken();
		
		String[] query = file_path.split("\\?"); //split on "?"
		file_path = query[0]; 
        return file_path; 
	}
	
	public String getReqMethod() {
		String req_items[] = request_line.split("\\s+");
		return req_items[0].trim();  
	}
	
	public String getReqVersion() {
		String req_items[] = request_line.split("\\s+");
		return req_items[2].trim(); 
	}
	
	public void buildStatusLine() {
		statusLine.append(version); 
		statusLine.append(status); 
		statusLine.append(CRLF);
	}
	
	public void buildHeaders() {
		responseHeaders.append(date); responseHeaders.append(CRLF);
		responseHeaders.append(server); responseHeaders.append(CRLF);
		responseHeaders.append(lastModified); responseHeaders.append(CRLF);	
		responseHeaders.append(contentType);	responseHeaders.append(CRLF);
		responseHeaders.append(contentLength);	responseHeaders.append(CRLF);
		responseHeaders.append("\n");
	}
}
