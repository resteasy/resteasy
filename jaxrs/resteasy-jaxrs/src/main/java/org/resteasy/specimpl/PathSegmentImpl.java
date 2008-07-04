package org.jboss.resteasy.specimpl;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PathSegmentImpl implements PathSegment
{
   private String path;
   private String original;
   private MultivaluedMap<String, String> matrixParameters = new MultivaluedMapImpl<String, String>();

   /**
    * @param path decode path segment
    */
   public PathSegmentImpl(String path)
   {
      this.original = path;
      this.path = path;
      int semicolon = path.indexOf(';');
      if (semicolon >= 0)
      {
         if (semicolon > 0) this.path = path.substring(0, semicolon);
         else this.path = "";
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

   public String getOriginal()
   {
      return original;
   }

   public PathSegmentImpl(String path, MultivaluedMap<String, String> matrixParameters)
   {
      this.path = path;
      this.matrixParameters = matrixParameters;
   }

   public String getPath()
   {
      return path;
   }

   public MultivaluedMap<String, String> getMatrixParameters()
   {
      return matrixParameters;
   }

   public static List<PathSegment> parseSegments(String path)
   {
      List<PathSegment> pathSegments = new ArrayList<PathSegment>();

      if (path.startsWith("/")) path = path.substring(1);
      if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
      String[] paths = path.split("/");
      for (String p : paths)
      {
         pathSegments.add(new PathSegmentImpl(p));
      }
      return pathSegments;
   }

}
