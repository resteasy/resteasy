package org.jboss.resteasy.keystone.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Roles implements Iterable<Role>, Serializable {

	@JsonProperty("roles")
	private Set<Role> roles = new HashSet<Role>();

   /**
	 * @return the roles
	 */
	public Set<Role> getRoles() {
		return roles;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Roles [roles=" + roles + "]";
	}

	@Override
	public Iterator<Role> iterator() {
		return roles.iterator();
	}
	
}
