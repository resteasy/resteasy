package org.jboss.resteasy.plugins.delegates;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.RuntimeDelegate;
import java.util.HashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MediaTypeHeaderDelegate implements RuntimeDelegate.HeaderDelegate
{
   public Object fromString(String type) throws IllegalArgumentException
   {
      return parse(type);
   }

   public static MediaType parse(String type)
   {
      String[] paths = type.split("/");
      if (paths.length != 2) throw new IllegalArgumentException("Failure parsing MediaType string: " + type);
      int idx = paths[1].indexOf(";");
      if (idx < 0)
      {
         return new MediaType(paths[0], paths[1]);
      }
      else
      {
         String major = paths[0];
         String params = paths[1].substring(idx + 1);
         String subtype = paths[1].substring(0, idx);
         HashMap<String, String> typeParams = new HashMap<String, String>();
         if (params.startsWith(";")) params = params.substring(1);
         String[] array = params.split(";");
         for (String param : array)
         {
            int pidx = param.indexOf("=");
            String name = param.substring(0, pidx);
            String val = param.substring(pidx + 1);
            typeParams.put(name, val);
         }
         return new MediaType(major, subtype, typeParams);
      }
   }

   public String toString(Object o)
   {
      MediaType type = (MediaType) o;
      String rtn = type.getType().toLowerCase() + "/" + type.getSubtype().toLowerCase();
      if (type.getParameters() == null || type.getParameters().size() == 0) return rtn;
      for (String name : type.getParameters().keySet())
      {
         String val = type.getParameters().get(name);
         rtn += ";" + name + "=" + val;
      }
      return rtn;
   }
}
