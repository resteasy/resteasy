/**
 * 
 */
package com.damnhandy.resteasy.grammar;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Ryan J. McDonough
 * Feb 18, 2007
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@XmlType(name = "resourceCollectionType")
public class ResourceCollection {

	@XmlElement(name = "resources",required = true, nillable = true)
	private Set<Reference> resources = new HashSet<Reference>();

	/**
	 * @return the resources
	 */
	public Set<Reference> getResources() {
		return resources;
	}

	/**
	 * @param resources the resources to set
	 */
	public void setResources(Set<Reference> resources) {
		this.resources = resources;
	}
	

	
}
