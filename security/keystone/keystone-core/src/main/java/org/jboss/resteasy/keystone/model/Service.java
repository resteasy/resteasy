package org.jboss.resteasy.keystone.model;

import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serializable;

@JsonRootName("OS-KSADM:service")
public class Service implements Serializable {

	private String id;
	
	private String type;
	
	private String name;
	
	private String description;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Service [id=" + id + ", type=" + type + ", name=" + name
				+ ", description=" + description + "]";
	}
	
}
