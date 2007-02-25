/**
 * 
 */
package com.damnhandy.resteasy.grammar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Ryan J. McDonough
 * Feb 12, 2007
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fault")
public class Fault {
	public static final String ERROR_STATUS = "javax.servlet.error.status_code";
	public static final String EXCEPTION_TYPE = "javax.servlet.error.exception_type";
	public static final String MESSAGE = "javax.servlet.error.message";
	public static final String EXCEPTION = "javax.servlet.error.exception";
	public static final String REQUEST_URI = "javax.servlet.error.request_uri";
	
	@XmlAttribute(required=true)
	private int statusCode;
	
	@XmlAttribute(required=true)
	private String exceptionType;
	
	@XmlElement(required=true)
	private String message;
	
	@XmlElement(required=true)
	private String requestUri;
	
	/**
	 * @param statusCode
	 * @param exceptionType
	 * @param message
	 * @param requestUri
	 */
	public Fault(int statusCode, String exceptionType, String message, String requestUri) {
		this.statusCode = statusCode;
		this.exceptionType = exceptionType;
		this.message = message;
		this.requestUri = requestUri;
	}
	
	/**
	 * 
	 *
	 */
	public Fault() {}
	/**
	 * @return the exceptionType
	 */
	public String getExceptionType() {
		return exceptionType;
	}
	/**
	 * @param exceptionType the exceptionType to set
	 */
	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the requestUri
	 */
	public String getRequestUri() {
		return requestUri;
	}
	/**
	 * @param requestUri the requestUri to set
	 */
	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}
	/**
	 * @return the statusCode
	 */
	public int getStatusCode() {
		return statusCode;
	}
	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
}
