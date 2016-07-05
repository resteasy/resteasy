package org.jboss.resteasy.keystone.model;

import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Map;

@JsonRootName("user")
public class StoredUser extends User
{

   private Map<String, String> credentials;

   public Map<String, String> getCredentials()
   {
      return credentials;
   }

   public void setCredentials(Map<String, String> credentials)
   {
      this.credentials = credentials;
   }

   public User toUser()
   {
      User user = new User();
      user.setId(getId());
      user.setEmail(getEmail());
      user.setEnabled(getEnabled());
      user.setName(getName());
      user.setUsername(getUsername());
      return user;
   }

}
