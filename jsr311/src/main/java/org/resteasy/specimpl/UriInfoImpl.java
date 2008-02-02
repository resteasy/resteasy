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
   private URI absolutePath;
   private URI absolutePathWithQueryString;
   private URI baseURI;

   public static void main(String[] args)
   {
      String val = "http://foo.com/hello/world/dude";
      val = val.substring(0, val.indexOf("world/dude"));
      System.out.print(val);
   }

   public UriInfoImpl(URI absolutePath, String path, String queryString)
   {
      this.path = path;
      this.absolutePath = absolutePath;
      if (queryString == null) this.absolutePathWithQueryString = absolutePath;
      else
      {
         this.absolutePathWithQueryString = URI.create(absolutePath.toString() + "?" + queryString);
      }
      if (path.trim().equals("")) baseURI = absolutePath;
      else
      {
         String abs = absolutePath.toString();
         abs = abs.substring(0, abs.indexOf(path));
         baseURI = URI.create(abs);
      }

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
      return absolutePathWithQueryString;
   }

   public UriBuilder getRequestUriBuilder()
   {
      return UriBuilder.fromUri(absolutePathWithQueryString);
   }

   public URI getAbsolutePath()
   {
      return absolutePath;
   }

   public UriBuilder getAbsolutePathBuilder()
   {
      return UriBuilder.fromUri(absolutePath);
   }

   public URI getBaseUri()
   {
      return baseURI;
   }

   public UriBuilder getBaseUriBuilder()
   {
      return UriBuilder.fromUri(baseURI);
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
