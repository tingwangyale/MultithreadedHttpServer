package Tests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import edu.upenn.cis.cis555.webserver.Handler;

public class XmlParserTest {
	public static void main(String[] args) {
		
		Date time = new Date();
		String webdotxml = "C:/Users/Ting/Google Drive/MCIT program/CIS555/workspace/HW1/WEB-INF/web.xml";
		HashMap<String, String> servletsMapping= new HashMap<>(); //maps url to servlet
		
		if (webdotxml != null) {
			try {
				Handler handler = new Handler();
				File file = new File(webdotxml);
				if (file.exists() == false) {
					System.err.println("error: cannot find " + file.getPath());
					System.exit(-1);
				}
				SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
				parser.parse(file, handler);
				
				servletsMapping = handler.getServletsMapping(); 
				// TODO Create load-on-startup servlets
				
				printServletsMapping(servletsMapping);
				
			} catch (IOException io) {
				io.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} 
		}
	}
	
	public static void printServletsMapping(HashMap<String, String> map) {
		for (String key: map.keySet()) {
			System.out.println("url: " + key);
			System.out.println("servlet-name: " + map.get(key));
			System.out.println();
		}
	}
}
