package Tests;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Scanner;

public class RequestedFileTest {
	
	public static void main(String[] args) {
		try { 			
			String resource_path = "/CIS555/index.html";
			String root_path = "C:/Users/Ting/Desktop";
			
			StringBuilder entityBody = new StringBuilder(); 
			
			resource_path = URLDecoder.decode(resource_path, "UTF-8");
			 
			File root = new File(root_path);
			File requested_file = new File(root, resource_path).getCanonicalFile();
			
			if (!requested_file.exists()) {
				//throw 404 Not Found Error
				System.out.println("Not Found!");
				return;
			}
			
			if (requested_file.isDirectory()) {
				String[] files = requested_file.list();
				entityBody.append("<html><body>");
				for (String f: files) {
					entityBody.append("<p>"); 
					entityBody.append(f);
					entityBody.append("</p>");
				}
				entityBody.append("</body></html>");
				System.out.println(entityBody.toString());

			} else {
				Scanner in = new Scanner(requested_file);
				while (in.hasNextLine()) {
					entityBody.append(in.nextLine());
				}
				System.out.println(entityBody.toString()); 
			}
	
		} catch (UnsupportedEncodingException e) {
			//TODO throw 500 Internal Server Error
			System.out.println(e);
		} catch (IOException io) {
			System.out.println(io);
		}
	}
}
