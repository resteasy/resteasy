package org.jboss.resteasy.links.test;

import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.links.ResourceID;

@XmlRootElement
public class ResourceIdBook extends IdBook{

	@ResourceID
	private String name;
	public ResourceIdBook() {
	}

	public ResourceIdBook(String name) {
		this.name = name;
	}

}
