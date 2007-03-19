/**
 * 
 */
package com.damnhandy.resteasy.core;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Ryan J. McDonough
 * Jan 21, 2007
 *
 */
public class MethodMapping<T> {

	private final Method method;
	private final Map<String,Class<?>> parameterMap;
    private String responseMediaType = "application/xml";
    private Class<T> requestRespresentationType = null;
    private String requestMediaType = "application/xml";
    private String requestRespresentationId;
    private int responseCode;
    private int failureResponseCode;
	/**
	 * @return the failureResponseCode
	 */
	protected int getFailureResponseCode() {
		return failureResponseCode;
	}
	/**
	 * @param failureResponseCode the failureResponseCode to set
	 */
	protected void setFailureResponseCode(int failureResponseCode) {
		this.failureResponseCode = failureResponseCode;
	}
	/**
	 * @return the responseCode
	 */
	protected int getResponseCode() {
		return responseCode;
	}
	/**
	 * @param responseCode the responseCode to set
	 */
	protected void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	/**
	 * @param method
	 * @param paramTypes
	 * @param paramNames
	 * @param requestRespresentationId
	 */
	protected MethodMapping(final Method method,final Map<String,Class<?>> parameterMap) {
		this.method = method;
		this.parameterMap = parameterMap;
	}
	/**
	 * @return the requestMediaType
	 */
	public String getRequestMediaType() {
		return requestMediaType;
	}
	/**
	 * @param requestMediaType the requestMediaType to set
	 */
	public void setRequestMediaType(String requestMediaType) {
		this.requestMediaType = requestMediaType;
	}
	/**
	 * @return the requestRespresentationName
	 */
	public String getRequestRespresentationId() {
		return requestRespresentationId;
	}
	/**
	 * @param requestRespresentationName the requestRespresentationName to set
	 */
	public void setRequestRespresentationId(String requestRespresentationId) {
		this.requestRespresentationId = requestRespresentationId;
	}
	/**
	 * @return the requestRespresentationType
	 */
	public Class<T> getRequestRespresentationType() {
		return requestRespresentationType;
	}
	/**
	 * @param requestRespresentationType the requestRespresentationType to set
	 */
	public void setRequestRespresentationType(Class<T> requestRespresentationType) {
		this.requestRespresentationType = requestRespresentationType;
	}
	/**
	 * @return the responseMediaType
	 */
	public String getResponseMediaType() {
		return responseMediaType;
	}
	/**
	 * @param responseMediaType the responseMediaType to set
	 */
	public void setResponseMediaType(String responseMediaType) {
		this.responseMediaType = responseMediaType;
	}
	/**
	 * @return the method
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * @return the parameterMap
	 */
	public final Map<String, Class<?>> getParameterMap() {
		return parameterMap;
	}
	
	/**
	 * 
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(method.getName()).append("(");
		int i = 0;
		for(Entry<String,Class<?>> entry : parameterMap.entrySet()) {
			b.append(entry.getKey()).append(":");
			if(entry.getKey().equals(getRequestRespresentationId())) {
				b.append(getRequestRespresentationType().getSimpleName());
			} else {
				b.append(entry.getValue().getSimpleName());
			}
			if(i != (parameterMap.size() - 1)) {
				b.append(",");
			}
			i++;
		}
		b.append(");");
		return b.toString();
	}

}
