package org.jboss.resteasy.links.test;

import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class XmlIdBook extends IdBook {

	@XmlID
	private String name;
	public XmlIdBook() {
	}

	public XmlIdBook(String name) {
		this.name = name;
	}

}
