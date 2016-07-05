package org.jboss.resteasy.keystone.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class Projects implements Iterable<Project>, Serializable {

	@JsonProperty("projects")
	private List<Project> list;
	
	@JsonProperty("projects_links")
	private List<Link> links;

   public void setList(List<Project> list)
   {
      this.list = list;
   }

   /**
	 * @return the list
	 */
	public List<Project> getList() {
		return list;
	}

	/**
	 * @return the links
	 */
	public List<Link> getLinks() {
		return links;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Projects [list=" + list + ", links=" + links + "]";
	}

	@Override
	public Iterator<Project> iterator() {
		return list.iterator();
	}
	
}
