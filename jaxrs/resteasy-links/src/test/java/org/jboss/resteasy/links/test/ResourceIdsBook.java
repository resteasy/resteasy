package org.jboss.resteasy.links.test;

import org.jboss.resteasy.links.ResourceIDs;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@ResourceIDs({"namea", "nameb"})
public class ResourceIdsBook extends IdBook{

	private String namea;
	private String nameb;
	public ResourceIdsBook() {
	}

	public ResourceIdsBook(String namea, String nameb) {
		this.namea = namea;
		this.nameb = nameb;
	}

}
