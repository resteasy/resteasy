package com.damnhandy.resteasy.criteria;
/**
 * 
 * @author Ryan J. McDonough
 * Feb 6, 2007
 *
 */
public enum Operation {
	LE("<=");
	
	private String value;
	private Operation(String value) {
		this.value = value;
	}
	

	
	/** 
	 *
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
	
}
