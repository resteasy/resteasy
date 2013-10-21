package org.jboss.resteasy.plugins.delegates;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class EntityTagDelegate implements RuntimeDelegate.HeaderDelegate<EntityTag>
{
   public EntityTag fromString(String value) throws IllegalArgumentException
   {
      if (value == null) throw new IllegalArgumentException("value of EntityTag is null");
      if (value.startsWith("\""))
      {
         value = value.substring(1);
      }
      if (value.endsWith("\""))
      {
         value = value.substring(0, value.length() - 1);
      }
      if (value.startsWith("W/"))
      {
         return new EntityTag(value.substring(2), true);
      }
      return new EntityTag(value);
   }

   public String toString(EntityTag value)
   {
      String weak = value.isWeak() ? "W/" : "";
      return weak + '"' + value.getValue() + '"';
   }

}
