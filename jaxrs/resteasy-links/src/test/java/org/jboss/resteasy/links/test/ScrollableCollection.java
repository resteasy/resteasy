package org.jboss.resteasy.links.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.links.ResourceFacade;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ScrollableCollection implements ResourceFacade<Comment> {

	private String id;
	@XmlAttribute
	private int start;
	@XmlAttribute
	private int totalRecords;
	@XmlElement
	private List<Comment> comments = new ArrayList<Comment>();
	@XmlElement
	private RESTServiceDiscovery rest;

	public ScrollableCollection() {}
	
	public ScrollableCollection(String id, int start, int totalRecords,
			List<Comment> comments) {
		this.id = id;
		this.start = start;
		this.totalRecords = totalRecords;
		this.comments.addAll(comments);
	}

	public Class<Comment> facadeFor() {
		return Comment.class;
	}

	public Map<String, ? extends Object> pathParameters() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		return map;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
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

}
