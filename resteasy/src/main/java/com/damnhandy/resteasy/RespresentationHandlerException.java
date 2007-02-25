/**
 * 
 */
package com.damnhandy.resteasy;

/**
 * @author Ryan J. McDonough
 * Nov 14, 2006
 *
 */
public class RespresentationHandlerException extends
		HttpMethodInvocationException {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4951129754664250299L;

	/**
	 * 
	 */
	public RespresentationHandlerException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public RespresentationHandlerException(String message,int httpStatusCode) {
		super(message,httpStatusCode);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public RespresentationHandlerException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RespresentationHandlerException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
