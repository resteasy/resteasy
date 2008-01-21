package org.resteasy.plugins.delegates;

import javax.ws.rs.ext.RuntimeDelegate;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MediaTypeHeaderDelegate implements RuntimeDelegate.HeaderDelegate
{
   public Object fromString(String type) throws IllegalArgumentException
   {
        String[] paths = type.split("/");
        return new MediaType(paths[0], paths[1]);   }

   public String toString(Object o)
   {
      MediaType type = (MediaType)o;
      return type.getType().toLowerCase()+"/"+type.getSubtype().toLowerCase();
   }
}
