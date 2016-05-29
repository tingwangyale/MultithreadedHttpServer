package edu.upenn.cis.cis555.webserver;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;


public class Handler extends DefaultHandler {
	
	private int tag = 0; 
	private String timeout = null;  
	private String m_servletName = null; //stores servlet name for url mapping
	private String m_paramName; //stores a parameter's name for entering the name, value pair
	HashMap<String,String> m_servlets; //maps servlet names to class, i.e. <servlet-class>
	HashMap<String,String> m_servletsMapping; //maps url to servlet name, i.e. <url-pattern>
	HashMap<String,String> m_contextParams; //stores the context parameters for the web app, i.e. <context-param>
	HashMap<String,HashMap<String,String>> m_servletParams; //stores the config parameters for each servlet, i.e. <init-param>
	
	
	public Handler() { 
		m_servlets = new HashMap<>(); 
		m_servletsMapping = new HashMap<>(); 
		m_contextParams = new HashMap<>(); 
		m_servletParams = new HashMap<>(); 
	}
	
	/*
	 * Method called at the start and end of a document element
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.compareTo("servlet-name") == 0) {
			tag = (tag == 6)? 30:1; 
		} else if (qName.compareTo("servlet-class") == 0) {
			tag = 2;
		} else if (qName.compareTo("context-param") == 0) {
			tag = 3;
		} else if (qName.compareTo("init-param") == 0) {
			tag = 4;
		} else if (qName.compareTo("url-pattern") == 0) {
			tag = 5;
		} else if (qName.compareTo("session-timeout") == 0) {
			tag = 6;
		} else if (qName.compareTo("param-name") == 0) {
			tag = (tag == 3) ? 10 : 20;
		} else if (qName.compareTo("param-value") == 0) {
			tag = (tag == 10) ? 11 : 21;
		} 
		// TODO robust checking: 
		// param-value must have param-name first
		// url patterns must have a servlet-name first
	}
	
	/*
	 * Method called with the text contents in between the start and end tags
	 * Take actions depending on the type of tag
	 */
	public void characters(char[] ch, int start, int length) {
		String value = new String(ch, start, length);
		
		if (tag == 1) {		//<servlet-name>, set servlet name
			m_servletName = value;
			tag = 0;
		} else if (tag == 2) {	//<servlet-class>, map servlet name to class
			m_servlets.put(m_servletName, value);
			tag = 0;
		} else if (tag == 5) {	//<url-pattern>, map url(s) to servlet name
			m_servletsMapping.put(value, m_servletName);
			tag = 0;
		} else if (tag == 6) { //<session-timeout>
			timeout = value;
			tag = 0;
		} else if (tag == 10 || tag == 20) { //<param-name> for <context-param> and <init-param>
			m_paramName = value;
		} else if (tag == 11) {		//store name and value in <context-param>
			if (m_paramName == null) {
				System.err.println("Context parameter value '" + value + "' without name");
				System.exit(-1);
			}
			m_contextParams.put(m_paramName, value); 
			m_paramName = null;
			tag = 0;
		} else if (tag == 21) {		//store name and value in <init-param>
			if (m_paramName == null) {
				System.err.println("Servlet parameter value '" + value + "' without name");
				System.exit(-1);
			}
			HashMap<String,String> p = m_servletParams.get(m_servletName); 
			if (p == null) {
				p = new HashMap<String,String>();
				m_servletParams.put(m_servletName, p);
			}
			p.put(m_paramName, value);
			m_paramName = null;
			tag = 0;
		}
	}
	
	public HashMap<String, String> getServletsMapping() {
		return m_servletsMapping; 
	}
	
	public HashMap<String, String> getServlets() {
		return  m_servlets;
	}
}
