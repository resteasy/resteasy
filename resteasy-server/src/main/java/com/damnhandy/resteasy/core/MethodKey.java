package com.damnhandy.resteasy.core;

import com.damnhandy.resteasy.annotations.HttpMethod;

/**
 * 
 * @author Ryan J. McDonough
 * Feb 2, 2007
 *
 */
public class MethodKey {
	private String method;
	private String discriminator;
	/**
	 * @param method
	 * @param discriminator
	 */
	public MethodKey(String method, String discriminator) {
		this(method);
		this.discriminator = discriminator;
	}
	
	/**
	 * 
	 * @param method
	 */
	public MethodKey(String method) {
		this.method = method;
	}

	/** 
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((discriminator == null) ? 0 : discriminator.hashCode());
		result = PRIME * result + ((method == null) ? 0 : method.hashCode());
		return result;
	}

	/** 
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(obj instanceof MethodKey) {
			final MethodKey key = (MethodKey) obj;
			if(key.discriminator == null && this.discriminator == null) {
				return key.method.equals(this.method);
			} else {
				return key.discriminator.equals(this.discriminator) &&
					   key.method.equals(this.method);
			}
		}
		return false;
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(method);
		if(discriminator != null) {
			b.append("[?").append(HttpMethod.DISCRIMINATOR_KEY).append("=").append(discriminator).append("]");
		}
		return b.toString();
	}
	
}
