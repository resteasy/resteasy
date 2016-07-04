package org.jboss.resteasy.links.test;

import org.jboss.resteasy.links.ParentResource;
import org.jboss.resteasy.links.RESTServiceDiscovery;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Comment {
	public int id;
	@XmlElement
	public String text;
	@ParentResource
	public Book book;

	@XmlElement
	// These both fail deserialisation for some reason
//	@XmlElement(name = "link", namespace = "http://www.w3.org/2005/Atom")
//	@XmlElementRef
	private RESTServiceDiscovery rest;

	public Comment() {
	}

	public Comment(int id, String text, Book book) {
		this.id = id;
		this.text = text;
		this.book = book;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}

	public RESTServiceDiscovery getRest() {
		return rest;
	}

	public void setRest(RESTServiceDiscovery rest) {
		this.rest = rest;
	}

	// JAXB wants an ID to be a String...
	@XmlAttribute
	@XmlID
	public String getXMLID(){
		return Integer.toString(id);
	}
}
