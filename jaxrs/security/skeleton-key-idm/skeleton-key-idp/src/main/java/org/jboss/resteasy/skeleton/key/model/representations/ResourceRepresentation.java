package org.jboss.resteasy.skeleton.key.model.representations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
* @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
* @version $Revision: 1 $
*/
public class ResourceRepresentation
{
   protected String self; // link
   protected String name;
   protected String baseUrl;
   protected boolean tokenAuthRequired;
   protected Set<String> roles;
   protected List<RoleMappingRepresentation> roleMappings;
   protected List<ScopeMappingRepresentation> scopeMappings;

   public String getSelf()
   {
      return self;
   }

   public void setSelf(String self)
   {
      this.self = self;
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

   public boolean isTokenAuthRequired()
   {
      return tokenAuthRequired;
   }

   public void setTokenAuthRequired(boolean tokenAuthRequired)
   {
      this.tokenAuthRequired = tokenAuthRequired;
   }

   public Set<String> getRoles()
   {
      return roles;
   }

   public void setRoles(Set<String> roles)
   {
      this.roles = roles;
   }

   public ResourceRepresentation role(String role)
   {
      if (this.roles == null) this.roles = new HashSet<String>();
      this.roles.add(role);
      return this;
   }

   public List<RoleMappingRepresentation> getRoleMappings()
   {
      return roleMappings;
   }

   public RoleMappingRepresentation roleMapping(String username)
   {
      RoleMappingRepresentation mapping = new RoleMappingRepresentation();
      mapping.setUsername(username);
      if (roleMappings == null) roleMappings = new ArrayList<RoleMappingRepresentation>();
      roleMappings.add(mapping);
      return mapping;
   }

   public List<ScopeMappingRepresentation> getScopeMappings()
   {
      return scopeMappings;
   }

   public ScopeMappingRepresentation scopeMapping(String username)
   {
      ScopeMappingRepresentation mapping = new ScopeMappingRepresentation();
      mapping.setUsername(username);
      if (scopeMappings == null) scopeMappings = new ArrayList<ScopeMappingRepresentation>();
      scopeMappings.add(mapping);
      return mapping;
   }


}
