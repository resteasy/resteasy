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
 * A light-weight class that can be used to return only a sub-set of the data 
 * used by an XML resources. It is useful for situtions where an XML resource
 * contains several fields and you only need to display a name and an ID in
 * a search results list.
 * 
 * @author Ryan J. McDonough
 * Feb 18, 2007
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resourceReferenceType")
public class ResourceReference {
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
