package org.jboss.resteasy.keystone.model;

import java.io.Serializable;

public class Link implements Serializable {

	private String rel;
	
	private String href;
	
	private String type;

	/**
	 * @return the rel
	 */
	public String getRel() {
		return rel;
	}

	/**
	 * @return the href
	 */
	public String getHref() {
		return href;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Link [rel=" + rel + ", href=" + href + ", type=" + type + "]";
	}
	
}
