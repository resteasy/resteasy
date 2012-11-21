package org.jboss.resteasy.skeleton.key.model.data;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Resource
{
   protected String id;
   protected String name;
   protected String baseUrl;
   protected boolean tokenAuthRequired;
   protected Set<String> roleIds = new HashSet<String>();
   protected Set<String> roleMappingIds = new HashSet<String>();

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getBaseUrl()
   {
      return baseUrl;
   }

   public void setBaseUrl(String baseUrl)
   {
      this.baseUrl = baseUrl;
   }

   public Set<String> getRoleIds()
   {
      return roleIds;
   }

   public Set<String> getRoleMappingIds()
   {
      return roleMappingIds;
   }

   public boolean isTokenAuthRequired()
   {
      return tokenAuthRequired;
   }

   public void setTokenAuthRequired(boolean tokenAuthRequired)
   {
      this.tokenAuthRequired = tokenAuthRequired;
   }
}
