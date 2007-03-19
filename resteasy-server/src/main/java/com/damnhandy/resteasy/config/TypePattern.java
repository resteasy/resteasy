/**
 * 
 */
package com.damnhandy.resteasy.config;

import java.io.Serializable;

/**
 * @author Ryan J. McDonough
 * Feb 13, 2007
 *
 */
public class TypePattern implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5265326125353159685L;
	private Class<?> type;
	private String regExPattern;
	
	/**
	 * @return the regExPattern
	 */
	public String getRegExPattern() {
		return regExPattern;
	}
	/**
	 * @param regExPattern the regExPattern to set
	 */
	public void setRegExPattern(final String regExPattern) {
		this.regExPattern = regExPattern;
	}
	/**
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(final Class<?> type) {
		this.type = type;
	}

}
