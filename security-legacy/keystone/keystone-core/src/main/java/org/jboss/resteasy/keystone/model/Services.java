package org.jboss.resteasy.keystone.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class Services implements Iterable<Service>,  Serializable {

	@JsonProperty("OS-KSADM:services")
	private List<Service> list;

	/**
	 * @return the list
	 */
	public List<Service> getList() {
		return list;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Services [list=" + list + "]";
	}

	@Override
	public Iterator<Service> iterator() {
		return list.iterator();
	}
	
}
