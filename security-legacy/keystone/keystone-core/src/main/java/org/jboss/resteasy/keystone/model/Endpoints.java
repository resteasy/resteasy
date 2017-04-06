package org.jboss.resteasy.keystone.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class Endpoints implements Iterable<Endpoint>, Serializable {

	@JsonProperty("endpoints")
	private List<Endpoint> list;

	/**
	 * @return the list
	 */
	public List<Endpoint> getList() {
		return list;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Endpoints [list=" + list + "]";
	}

	@Override
	public Iterator<Endpoint> iterator() {
		return list.iterator();
	}
	
}
