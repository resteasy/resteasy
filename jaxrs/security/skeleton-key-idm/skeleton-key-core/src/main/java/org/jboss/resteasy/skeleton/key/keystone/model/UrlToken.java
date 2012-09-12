package org.jboss.resteasy.skeleton.key.keystone.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonRootName;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UrlToken implements Serializable {
   @JsonProperty("pid")
   private String projectId;
   @JsonProperty("exp")
   private Calendar expires;
   @JsonProperty("uid")
   private String userId;

   @JsonProperty("r")
   private Set<String> roles = new HashSet<String>();

   public String getProjectId()
   {
      return projectId;
   }

   public void setProjectId(String projectId)
   {
      this.projectId = projectId;
   }

   public Calendar getExpires()
   {
      return expires;
   }

   public void setExpires(Calendar expires)
   {
      this.expires = expires;
   }

   public String getUserId()
   {
      return userId;
   }

   public void setUserId(String userId)
   {
      this.userId = userId;
   }

   public Set<String> getRoles()
   {
      return roles;
   }

   public boolean expired()
   {
      return expires.getTime().getTime() < System.currentTimeMillis();
   }

}
