/**
 * 
 */
package com.damnhandy.resteasy.common;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Ryan J. McDonough
 * @since 1.0
 */
public class AbstractAcceptHeader {

	private Map<String,Float> values = new TreeMap<String,Float>();
	
	
	private static class AcceptValue {
		private String primaryType;
		private String subType;
		private Float qvalue;
	}
}
