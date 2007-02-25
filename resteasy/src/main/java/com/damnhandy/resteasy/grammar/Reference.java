/**
 * 
 */
package com.damnhandy.resteasy.grammar;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Ryan J. McDonough
 * Feb 18, 2007
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resourceReferenceType")
public class Reference {
	@XmlAttribute(required=true)
	private URL location;
	private Map<String,Object> properties = new HashMap<String,Object>();
	/**
	 * @return the location
	 */
	public URL getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(URL location) {
		this.location = location;
	}
	
	/**
	 * @return the properties
	 */
	public Map<String, Object> getProperties() {
		return properties;
	}
	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	
}
