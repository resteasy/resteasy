package org.resteasy.specimpl;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UriInfoImpl implements UriInfo
{
   private String path;
   private MultivaluedMap<String, String> queryParameters;
   private MultivaluedMap<String, String> templateParameters;
   private List<PathSegment> pathSegments;

   public UriInfoImpl(String path)
   {
      this.path = path;
      pathSegments = new ArrayList<PathSegment>();
      queryParameters = new MultivaluedMapImpl<String, String>();
      templateParameters = new MultivaluedMapImpl<String, String>();

      if (path.startsWith("/")) path = path.substring(1);
      String[] paths = path.split("/");
      for (String p : paths)
      {
         pathSegments.add(new PathSegmentImpl(p));
      }

   }

   public String getPath()
   {
      return path;
   }

   public String getPath(boolean decode)
   {
      throw new RuntimeException("NOT IMPLEMENTED");
   }

   public List<PathSegment> getPathSegments()
   {
      return pathSegments;
   }

   public List<PathSegment> getPathSegments(boolean decode)
   {
      throw new RuntimeException("NOT IMPLEMENTED");
   }

   public URI getRequestUri()
   {
      throw new RuntimeException("NOT IMPLEMENTED");
   }

   public UriBuilder getRequestUriBuilder()
   {
      throw new RuntimeException("NOT IMPLEMENTED");
   }

   public URI getAbsolutePath()
   {
      throw new RuntimeException("NOT IMPLEMENTED");
   }

   public UriBuilder getAbsolutePathBuilder()
   {
      throw new RuntimeException("NOT IMPLEMENTED");
   }

   public URI getBaseUri()
   {
      throw new RuntimeException("NOT IMPLEMENTED");
   }

   public UriBuilder getBaseUriBuilder()
   {
      throw new RuntimeException("NOT IMPLEMENTED");
   }

   public MultivaluedMap<String, String> getTemplateParameters()
   {
      return templateParameters;
   }

   public MultivaluedMap<String, String> getTemplateParameters(boolean decode)
   {
      throw new RuntimeException("NOT IMPLEMENTED");
   }

   public MultivaluedMap<String, String> getQueryParameters()
   {
      return queryParameters;
   }

   public MultivaluedMap<String, String> getQueryParameters(boolean decode)
   {
      throw new RuntimeException("NOT IMPLEMENTED");
   }

}
