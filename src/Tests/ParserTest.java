package Tests;

import edu.upenn.cis.cis555.webserver.WebXmlParser;

public class ParserTest {
	public static void main(String[] args) {
		String xml_path = "C:/Users/Ting/Google Drive/MCIT program/CIS555/workspace/HW1/WEB-INF/web.xml";
		WebXmlParser parser = new WebXmlParser(xml_path);
		
		String servletClassName = WebXmlParser.servletClassName("/cis555/search.html"); 
		System.out.println(servletClassName);
	}
}
