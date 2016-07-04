package org.jboss.resteasy.keystone.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DomainToken implements Serializable {
   @JsonProperty("uid")
   private String userId;
   @JsonProperty("e")
   private Calendar expires;

   public static class ProjectRoles implements Serializable
   {
      private String pid;
      private Set<String> roles = new HashSet<String>();

      public ProjectRoles()
      {
      }

      public ProjectRoles(String pid)
      {
         this.pid = pid;
      }

      public String getPid()
      {
         return pid;
      }
   }

   @JsonProperty("p")
   private List<ProjectRoles> projects = new ArrayList<ProjectRoles>();

   public List<ProjectRoles> getProjects()
   {
      return projects;
   }

   public boolean expired()
   {
      return expires.getTime().getTime() < System.currentTimeMillis();
   }

}
