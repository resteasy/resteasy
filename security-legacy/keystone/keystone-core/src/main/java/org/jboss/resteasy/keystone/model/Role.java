package org.jboss.resteasy.keystone.model;

import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serializable;

@JsonRootName("role")
public class Role implements Serializable {

	private String id;
	
	private String name;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

   public void setId(String id)
   {
      this.id = id;
   }

   /**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Role role = (Role) o;

      if (id != null ? !id.equals(role.id) : role.id != null) return false;
      if (name != null ? !name.equals(role.name) : role.name != null) return false;

      return true;
   }

   @Override
   public int hashCode()
   {
      int result = id != null ? id.hashCode() : 0;
      result = 31 * result + (name != null ? name.hashCode() : 0);
      return result;
   }
}
