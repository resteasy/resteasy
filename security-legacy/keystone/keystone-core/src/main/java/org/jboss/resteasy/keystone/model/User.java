package org.jboss.resteasy.keystone.model;

import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serializable;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@JsonRootName("user")
public class User implements Serializable
{
   private String id;
   private String username;
   private String name;
   private String email;
   private Boolean enabled;

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
    * @return the username
    */
   public String getUsername() {
      return username;
   }

   /**
    * @param username the username to set
    */
   public void setUsername(String username) {
      this.username = username;
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

   /**
    * @return the email
    */
   public String getEmail() {
      return email;
   }

   /**
    * @param email the email to set
    */
   public void setEmail(String email) {
      this.email = email;
   }

   /**
    * @return the enabled
    */
   public Boolean getEnabled() {
      return enabled;
   }

   /**
    * @param enabled the enabled to set
    */
   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
   }

   /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", name=" + name + ", email=" + email
				+ ", enabled=" + enabled + "]";
	}
}
