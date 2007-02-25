/**
 * 
 */
package com.damnhandy.resteasy;

/**
 * @author ryan
 *
 */
public class HttpMethodInvocationException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7256792479313558295L;
	private int httpStatusCode = 500;

	public HttpMethodInvocationException() {
	}
	/**
	 * 
	 */
	public HttpMethodInvocationException(int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

	/**
	 * @param message
	 */
	public HttpMethodInvocationException(String message,int httpStatusCode) {
		super(message);
		this.httpStatusCode = httpStatusCode;
	}

	/**
	 * @param cause
	 */
	public HttpMethodInvocationException(Throwable cause) {
		super(cause);
	}
	/**
	 * @param cause
	 */
	public HttpMethodInvocationException(Throwable cause,int httpStatusCode) {
		super(cause);
		this.httpStatusCode = httpStatusCode;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public HttpMethodInvocationException(String message, int httpStatusCode,Throwable cause) {
		super(message, cause);
		this.httpStatusCode = httpStatusCode;
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public HttpMethodInvocationException(String message,Throwable cause) {
		super(message, cause);
	}


	/**
	 * @return the httpStatusCode
	 */
	protected int getHttpStatusCode() {
		return httpStatusCode;
	}

}
