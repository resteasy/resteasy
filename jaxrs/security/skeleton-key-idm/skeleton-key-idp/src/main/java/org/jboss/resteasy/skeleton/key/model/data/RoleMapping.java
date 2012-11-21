package org.jboss.resteasy.skeleton.key.model.data;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RoleMapping
{
   protected String id;
   protected String userid;
   protected Set<String> roleIds = new HashSet<String>();
   protected String surrogateId;

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public String getUserid()
   {
      return userid;
   }

   public void setUserid(String userid)
   {
      this.userid = userid;
   }

   public Set<String> getRoleIds()
   {
      return roleIds;
   }

   public void setRoleIds(Set<String> roleIds)
   {
      this.roleIds = roleIds;
   }

   public String getSurrogateId()
   {
      return surrogateId;
   }

   public void setSurrogateId(String surrogateId)
   {
      this.surrogateId = surrogateId;
   }
}
