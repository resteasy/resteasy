package com.damnhandy.resteasy.core;


/**
 * 
 * @author Ryan J. McDonough
 * @since 1.0
 * Jan 15, 2007
 *
 */
public class RestEasy {
	public static final String JNDI_PATTERN = "jndiPattern";
	public static final String ENTITYMANAGER_JNDI_NAME = "entityManagerJndiName";
	private static RestEasy instance = new RestEasy();
	
	private String jndiPattern;
	private String entityManagerJndiName;


	private RestEasy() { }
	
	/**
	 * @return the instance
	 */
	public static RestEasy instance() {
		return instance;
	}

	public void init() {
		ResourceDispatcher.getInstance().init();
	}
	
	/**
	 * @return the jndiPattern
	 */
	public final String getJndiPattern() {
		return jndiPattern;
	}

	/**
	 * @param jndiPattern the jndiPattern to set
	 */
	public void setJndiPattern(String jndiPattern) {
		this.jndiPattern = jndiPattern;
	}	
	
	/**
	 * @return the entityManagerJndiName
	 */
	public final String getEntityManagerJndiName() {
		return entityManagerJndiName;
	}

	/**
	 * @param entityManagerJndiName the entityManagerJndiName to set
	 */
	public void setEntityManagerJndiName(String entityManagerJndiName) {
		this.entityManagerJndiName = entityManagerJndiName;
	}
}
