package org.jboss.resteasy.plugins.delegates;

import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.ext.RuntimeDelegate;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class EntityTagDelegate implements RuntimeDelegate.HeaderDelegate<EntityTag>
{
   public static final EntityTagDelegate INSTANCE = new EntityTagDelegate();

   public EntityTag fromString(String value) throws IllegalArgumentException
   {
      if (value == null) throw new IllegalArgumentException(Messages.MESSAGES.entityTagValueNull());
      boolean weakTag = false;
      if (value.startsWith("W/"))
      {
         weakTag = true;
         value = value.substring(2);
      }
      if (value.startsWith("\""))
      {
         value = value.substring(1);
      }
      if (value.endsWith("\""))
      {
         value = value.substring(0, value.length() - 1);
      }
      return new EntityTag(value, weakTag);
   }

   public String toString(EntityTag value)
   {
      String weak = value.isWeak() ? "W/" : "";
      return weak + '"' + value.getValue() + '"';
   }

}
