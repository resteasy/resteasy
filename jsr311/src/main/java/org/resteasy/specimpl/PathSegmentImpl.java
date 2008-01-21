package org.resteasy.specimpl;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PathSegmentImpl implements PathSegment
{
   private String path;
   private MultivaluedMap<String, String> matrixParameters = new MultivaluedMapImpl<String, String>();

   public PathSegmentImpl(String path)
   {
      this.path = path;
      int semicolon = path.indexOf(';');
      if (semicolon >= 0)
      {
         String matrixParams = path.substring(semicolon + 1);
         String[] params = matrixParams.split(";");
         for (String param : params)
         {
            String[] namevalue = param.split("=");
            if (namevalue != null && namevalue.length > 0)
            {
               String name = namevalue[0];
               String value = "";
               if (namevalue.length > 1)
               {
                  value = namevalue[1];
               }
               matrixParameters.add(name, value);
            }
         }
      }
   }

   public String getPath()
   {
      return path;
   }

   public MultivaluedMap<String, String> getMatrixParameters()
   {
      return null;
   }
}
