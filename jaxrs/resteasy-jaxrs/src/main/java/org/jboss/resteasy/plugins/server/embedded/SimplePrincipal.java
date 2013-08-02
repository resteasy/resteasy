package org.jboss.resteasy.plugins.server.embedded;

import java.security.Principal;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SimplePrincipal implements Principal
{
   private String username;

   public SimplePrincipal(String username)
   {
      this.username = username;
   }

   public String getName()
   {
      return username;
   }

   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      SimplePrincipal that = (SimplePrincipal) o;

      if (username != null ? !username.equals(that.username) : that.username != null) return false;

      return true;
   }

   public int hashCode()
   {
      return (username != null ? username.hashCode() : 0);
   }
}
