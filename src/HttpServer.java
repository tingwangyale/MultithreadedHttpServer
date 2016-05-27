import java.io.*;
import java.net.*; 

public class HttpServer{
	
	public static void main(String args[]) throws Exception {
		//int port = Integer.parseInt(args[0]);
		//String root_dir = args[1]; 
		int port = 8080; 
		String root_dir = "C:/Users/Ting/Desktop";  
		ServerSocket serverSocket = new ServerSocket(port);
	
		while (true) {
			// Listen for a TCP connection request 
			Socket clientSocket = serverSocket.accept();
			System.out.println("Accepted connection: " + clientSocket);
			
			HttpRequest request = new HttpRequest(clientSocket, root_dir);
			Thread thread = new Thread(request);  
			thread.start(); 
		}
	}
}
