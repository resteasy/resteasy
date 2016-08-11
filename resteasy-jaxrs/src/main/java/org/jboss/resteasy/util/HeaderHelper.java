package org.jboss.resteasy.util;

import javax.ws.rs.core.MultivaluedMap;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HeaderHelper
{
   @SuppressWarnings(value = "unchecked")
   public static void setAllow(MultivaluedMap headers, String[] methods)
   {
      if (methods == null)
      {
         headers.remove("Allow");
         return;
      }
      StringBuilder builder = new StringBuilder();
      boolean isFirst = true;
      for (String l : methods)
      {
         if (isFirst)
         {
            isFirst = false;
         }
         else
         {
            builder.append(", ");
         }
         builder.append(l);
      }
      headers.putSingle("Allow", builder.toString());
   }

   @SuppressWarnings(value = "unchecked")
   public static void setAllow(MultivaluedMap headers, Set<String> methods)
   {
      if (methods == null)
      {
         headers.remove("Allow");
         return;
      }
      StringBuilder builder = new StringBuilder();
      boolean isFirst = true;
      for (String l : methods)
      {
         if (isFirst)
         {
            isFirst = false;
         }
         else
         {
            builder.append(", ");
         }
         builder.append(l);
      }
      headers.putSingle("Allow", builder.toString());
   }

}
