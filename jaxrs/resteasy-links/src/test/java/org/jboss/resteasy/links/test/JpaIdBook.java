package org.jboss.resteasy.links.test;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JpaIdBook extends IdBook{

	@Id
	private String name;
	
	public JpaIdBook() {
	}

	public JpaIdBook(String name) {
		this.name = name;
	}

}
