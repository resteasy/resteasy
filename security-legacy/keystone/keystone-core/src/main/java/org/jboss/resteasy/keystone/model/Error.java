package org.jboss.resteasy.keystone.model;

import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serializable;

@JsonRootName("error")
public class Error implements Serializable {

	private Integer code;
	
	private String title;
	
	private String message;

	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Error [code=" + code + ", title=" + title + ", message="
				+ message + "]";
	}
	
}
