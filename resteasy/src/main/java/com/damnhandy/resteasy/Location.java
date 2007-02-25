/**
 * 
 */
package com.damnhandy.resteasy;

/**
 * @author Ryan J. McDonough
 * Feb 24, 2007
 *
 */
public class Location {

	private String id;
	private int status;
	/**
	 * @param id
	 * @param status
	 */
	public Location(String id, int status) {
		this.id = id;
		this.status = status;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	protected void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	protected void setStatus(int status) {
		this.status = status;
	}
	
	/**
	 * 
	 * @param host
	 * @param path
	 * @return
	 */
	public String buildLocationString(String protocol,String host,String port,String path) {
		StringBuilder b = new StringBuilder();
		b.append(protocol).append("://").append(host);
		if(protocol != null && protocol.length() > 0) {
			
		}
		return b.toString();
	}
	
	
	
}
