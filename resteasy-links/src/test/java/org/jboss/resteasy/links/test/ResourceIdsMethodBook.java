package org.jboss.resteasy.links.test;

import org.jboss.resteasy.links.ResourceIDs;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@ResourceIDs({"namea", "nameb"})
public class ResourceIdsMethodBook extends IdBook{

	private String _namea;
	private String _nameb;
	
	public ResourceIdsMethodBook() {
	}

	public ResourceIdsMethodBook(String namea, String nameb) {
		this._namea = namea;
		this._nameb = nameb;
	}

	public String getNamea() {
		return _namea;
	}

	public void setNamea(String namea) {
		this._namea = namea;
	}

	public String getNameb() {
		return _nameb;
	}

	public void setNameb(String nameb) {
		this._nameb = nameb;
	}

}
