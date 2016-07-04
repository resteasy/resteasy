package org.jboss.resteasy.links.test;

import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.annotations.providers.jaxb.json.XmlNsMap;
import org.jboss.resteasy.links.RESTServiceDiscovery;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@Mapped(namespaceMap = @XmlNsMap(jsonName = "atom", namespace = "http://www.w3.org/2005/Atom"))
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Book {
	@XmlAttribute
	private String author;
	@XmlID
	@XmlAttribute
	private String title;
	@XmlElement
	// These both fail deserialisation for some reason
//	@XmlElement(name = "link", namespace = "http://www.w3.org/2005/Atom")
//	@XmlElementRef
	private RESTServiceDiscovery rest;

	private List<Comment> comments = new ArrayList<Comment>();
	
	public Book(String title, String author) {
		this.author = author;
		this.title = title;
	}

	public Book() {
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public RESTServiceDiscovery getRest() {
		return rest;
	}

	public void setRest(RESTServiceDiscovery rest) {
		this.rest = rest;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public void addComment(int id, String text){
		comments.add(new Comment(id, text, this));
	}

	public Comment getComment(int commentId) {
		for(Comment c : comments)
			if(c.getId() == commentId)
				return c;
		return null;
	}
}
