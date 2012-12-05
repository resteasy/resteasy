package org.jboss.resteasy.skeleton.key;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jboss.resteasy.jwt.JsonWebToken;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SkeletonKeyToken extends JsonWebToken
{
   public static class Access
   {
      @JsonProperty("surrogates")
      protected Set<String> surrogates;
      @JsonProperty("roles")
      protected Set<String> roles;
      @JsonProperty("verify_caller")
      protected boolean verifyCaller;

      public Set<String> getSurrogates()
      {
         return surrogates;
      }

      public Access surrogates(Set<String> surrogates)
      {
         this.surrogates = surrogates;
         return this;
      }

      public Set<String> getRoles()
      {
         return roles;
      }

      public Access roles(Set<String> roles)
      {
         this.roles = roles;
         return this;
      }

      @JsonIgnore
      public boolean isUserInRole(String role)
      {
         if (roles == null) return false;
         return roles.contains(role);
      }

      @JsonIgnore
      public boolean hasSurrogate(String surrogate)
      {
         if (surrogates == null) return false;
         return surrogates.contains(surrogate);
      }

      public Access addSurrogate(String surrogate)
      {
         if (surrogates == null) surrogates = new HashSet<String>();
         surrogates.add(surrogate);
         return this;
      }
      public Access addRole(String role)
      {
         if (roles == null) roles = new HashSet<String>();
         roles.add(role);
         return this;
      }

      public boolean isVerifyCaller()
      {
         return verifyCaller;
      }

      public Access surrogateAuthRequired(boolean required)
      {
         this.verifyCaller = required;
         return this;
      }
   }

   @JsonProperty("realm_access")
   protected Access realmAccess;

   @JsonProperty("resource_access")
   protected Map<String, Access> resourceAccess = new HashMap<String, Access>();

   public Map<String, Access> getResourceAccess()
   {
      return resourceAccess;
   }

   @JsonIgnore
   public Access getResourceAccess(String resource)
   {
      return resourceAccess.get(resource);
   }

   public Access addAccess(String service)
   {
      Access token = new Access();
      resourceAccess.put(service, token);
      return token;
   }

   @Override
   public SkeletonKeyToken id(String id)
   {
      return (SkeletonKeyToken)super.id(id);
   }

   @Override
   public SkeletonKeyToken expiration(long expiration)
   {
      return (SkeletonKeyToken)super.expiration(expiration);
   }

   @Override
   public SkeletonKeyToken notBefore(long notBefore)
   {
      return (SkeletonKeyToken)super.notBefore(notBefore);
   }

   @Override
   public SkeletonKeyToken issuedAt(long issuedAt)
   {
      return (SkeletonKeyToken)super.issuedAt(issuedAt);
   }

   @Override
   public SkeletonKeyToken issuer(String issuer)
   {
      return (SkeletonKeyToken)super.issuer(issuer);
   }

   @Override
   public SkeletonKeyToken audience(String audience)
   {
      return (SkeletonKeyToken)super.audience(audience);
   }

   @Override
   public SkeletonKeyToken principal(String principal)
   {
      return (SkeletonKeyToken)super.principal(principal);
   }

   @Override
   public SkeletonKeyToken type(String type)
   {
      return (SkeletonKeyToken)super.type(type);
   }

   public Access getRealmAccess()
   {
      return realmAccess;
   }

   public void setRealmAccess(Access realmAccess)
   {
      this.realmAccess = realmAccess;
   }
}
