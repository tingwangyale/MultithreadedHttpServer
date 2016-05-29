package edu.upenn.cis.cis555.webserver;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class WebXmlParser {
	
	private static String web_xml_path; 
	private static HashMap<String, String> servletsMapping; //maps url to servlet
	private static HashMap<String, String> servlets; //maps servlet name to class name 
	
	public WebXmlParser(String webdotxml) {
		web_xml_path = webdotxml;
		parse(); 
	}
	
	public static void parse() {
		if (web_xml_path != null) {
			try {
				Handler handler = new Handler();
				File file = new File(web_xml_path);
				if (file.exists() == false) {
					System.err.println("error: cannot find " + file.getPath());
					System.exit(-1);
				}
				SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
				parser.parse(file, handler);
				
				servletsMapping = handler.getServletsMapping(); 
				servlets = handler.getServlets(); 
				
			} catch (IOException io) {
				io.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} 
		}
	}
	
	/* 
	 * Find corresponding class name given URL
	 * URL pattern -> servlet name -> servlet class  
	 */
	public static String servletClassName(String url) {
		String name = null, className = null; 
		if ((name = servletsMapping.get(url)) == null) {
			return null; 
		} else if ((className = servlets.get(name)) == null) {
			return null; 
		} else {
			return className; 
		}
	}
	
	public static HashMap<String, String> getServletsMapping() {
		return servletsMapping;
	}
	
	public static HashMap<String, String> getServlets() {
		return servlets; 
	}
}
