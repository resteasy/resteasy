package com.damnhandy.resteasy.core;

import com.damnhandy.resteasy.annotations.HttpMethod;

/**
 * 
 * @author Ryan J. McDonough
 * @since 1.0
 * Feb 2, 2007
 *
 */
public class MethodKey {
	private String method;
	private String discriminator;
	private String inputType;
	private String outputType;


	

	/**
	 * @param method
	 * @param discriminator
	 * @param inputType
	 * @param outputType
	 */
	protected MethodKey(String method, String discriminator, String inputType, String outputType) {
		this.method = method;
		this.discriminator = discriminator;
		this.inputType = inputType;
		this.outputType = outputType;
	}
	/**
	 * @return the inputType
	 */
	public String getInputType() {
		return inputType;
	}
	
	/**
	 * @return the inputType
	 */
	public String getOutputType() {
		return outputType;
	}
	/** 
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 0;
		result = PRIME * result + ((discriminator == null) ? 0 : discriminator.hashCode());
		result = PRIME * result + ((inputType == null) ? 0 : inputType.hashCode());
		result = PRIME * result + ((outputType == null) ? 0 : outputType.hashCode());
		result = PRIME * result + ((method == null) ? 0 : method.hashCode());
		
		return result;
	}

	/** 
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} 
		
		if(obj == null) {
			return false;
		}
		
		if(obj instanceof MethodKey) {
			final MethodKey methodKey = (MethodKey) obj;
			if((methodKey.discriminator != null && this.discriminator == null) ||
			   (methodKey.discriminator == null && this.discriminator != null)) {
				return false;
			}
			else if((methodKey.inputType != null && this.inputType == null) ||
					(methodKey.inputType == null && this.inputType != null)) {
				return false;
			}
			/*
			 * If
			 */
			else if((methodKey.discriminator == null && this.discriminator == null) &&
					(this.inputType == null && methodKey.inputType == null)) {
				return methodKey.method.equals(this.method) && 
					   methodKey.outputType.equals(this.outputType);
			}
			else if(methodKey.discriminator == null || this.discriminator == null) {
				return methodKey.method.equals(this.method) && 
					   methodKey.inputType.equals(this.inputType) &&
					   methodKey.outputType.equals(this.outputType);
			}
			else {
				return methodKey.method.equals(this.method) && 
				   	   methodKey.inputType.equals(this.inputType)  &&
					   methodKey.outputType.equals(this.outputType) &&
				   	   methodKey.discriminator.equals(this.discriminator);
			}
		}
		return false;
	}
	
	/**
	 * 
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(method);
		if(discriminator != null) {
			b.append("[?").append(HttpMethod.DISCRIMINATOR_KEY).append("=").append(discriminator).append("]");
		}
		b.append("[in=").append(getInputType()).append(",out=").append(getOutputType()).append("]");
		return b.toString();
	}
	

}
