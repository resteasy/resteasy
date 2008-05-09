package org.resteasy.specimpl;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UriInfoImpl implements UriInfo
{
   private String path;
   private String encodedPath;
   private MultivaluedMap<String, String> queryParameters;
   private MultivaluedMap<String, String> encodedQueryParameters;
   private MultivaluedMap<String, String> templateParameters;
   private MultivaluedMap<String, String> encodedTemplateParameters;
   private List<PathSegment> pathSegments;
   private List<PathSegment> encodedPathSegments;
   private URI absolutePath;
   private URI absolutePathWithQueryString;
   private URI baseURI;
   private String queryString;

   /**
    * @param absolutePath
    * @param path         decoded equivalent to HttpServletRequest.getPathInfo()
    * @param queryString  encoded query string of request
    */
   public UriInfoImpl(URI absolutePath, String path, String queryString)
   {
      this(absolutePath, path, queryString, PathSegmentImpl.parseSegments(path));
   }

   public UriInfoImpl(URI absolutePath, String path, String queryString, List<PathSegment> pathSegments)
   {
      this.path = path;
      this.absolutePath = absolutePath;
      this.queryParameters = new MultivaluedMapImpl<String, String>();
      this.encodedQueryParameters = new MultivaluedMapImpl<String, String>();
      extractParameters(queryString);
      this.templateParameters = new MultivaluedMapImpl<String, String>();
      this.pathSegments = pathSegments;
      this.queryString = queryString;


      if (queryString == null) this.absolutePathWithQueryString = absolutePath;
      else
      {
         this.absolutePathWithQueryString = URI.create(absolutePath.toString() + "?" + queryString);
      }
      if (path.trim().equals("")) baseURI = absolutePath;
      else
      {
         String abs = absolutePath.getPath();
         abs = abs.substring(0, abs.indexOf(path));
         if (!abs.endsWith("/")) abs += "/";
//         System.out.println("abs: " + abs);
//         System.out.println("absolutePath: " + absolutePath);
//         System.out.println("path: " + path);
         try
         {
            baseURI = UriBuilder.fromUri(absolutePath).replacePath(abs).build();

         }
         catch (Exception e)
         {
            throw new RuntimeException("URI value was: " + abs + " path: " + path, e);
         }
      }
   }

   // this is here for testing purposes todo remove it!
   public UriInfoImpl(List<PathSegment> pathSegments)
   {
      this.pathSegments = pathSegments;
   }

   public UriInfoImpl clone()
   {
      return new UriInfoImpl(absolutePath, path, queryString, pathSegments);
   }

   public String getPath()
   {
      return path;
   }

   public String getPath(boolean decode)
   {
      if (decode) return path;
      try
      {
         if (encodedPath == null)
         {
            String tmp = path.substring(1);
            String[] segments = tmp.split("/");
            encodedPath = "";
            for (String segment : segments)
            {
               encodedPath += "/" + URLEncoder.encode(segment, "UTF-8").replace("+", "%20");
            }
         }
      }
      catch (UnsupportedEncodingException e)
      {
         throw new RuntimeException(e);
      }
      return encodedPath;
   }

   public List<PathSegment> getPathSegments()
   {
      return pathSegments;
   }

   public List<PathSegment> getPathSegments(boolean decode)
   {
      if (decode) return pathSegments;
      if (encodedPathSegments != null) return encodedPathSegments;
      String p = getPath(false);
      encodedPathSegments = PathSegmentImpl.parseSegments(p);
      return encodedPathSegments;
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

   public MultivaluedMap<String, String> getPathParameters()
   {
      return templateParameters;
   }

   public MultivaluedMap<String, String> getPathParameters(boolean decode)
   {
      if (decode) return templateParameters;
      if (encodedTemplateParameters != null) return encodedTemplateParameters;

      encodedTemplateParameters = new MultivaluedMapImpl<String, String>();

      for (String key : templateParameters.keySet())
      {
         List<String> values = templateParameters.get(key);
         for (String value : values)
         {
            try
            {
               encodedTemplateParameters.add(key, URLEncoder.encode(value, "UTF-8").replace("+", "%20"));
            }
            catch (UnsupportedEncodingException e)
            {
               throw new RuntimeException(e);
            }
         }
      }

      return encodedTemplateParameters;
   }

   public MultivaluedMap<String, String> getQueryParameters()
   {
      return queryParameters;
   }

   public MultivaluedMap<String, String> getQueryParameters(boolean decode)
   {
      if (decode) return queryParameters;
      else return encodedQueryParameters;
   }

   protected void extractParameters(String queryString)
   {
      if (queryString == null || queryString.equals("")) return;

      String[] params = queryString.split("&");

      for (String param : params)
      {
         if (param.indexOf('=') >= 0)
         {
            String[] nv = param.split("=");
            try
            {
               String name = URLDecoder.decode(nv[0], "UTF-8");
               encodedQueryParameters.add(name, nv[1]);
               queryParameters.add(name, URLDecoder.decode(nv[1], "UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
               throw new RuntimeException(e);
            }
         }
         else
         {
            try
            {
               String name = URLDecoder.decode(param, "UTF-8");
               encodedQueryParameters.add(name, "");
               queryParameters.add(name, "");
            }
            catch (UnsupportedEncodingException e)
            {
               throw new RuntimeException(e);
            }
         }
      }
   }

   public UriBuilder getPlatonicRequestUriBuilder()
   {
      throw new RuntimeException("NOT IMPLEMENTED");
   }

   public String getPathExtension()
   {
      throw new RuntimeException("NOT IMPLEMENTED");
   }

   public List<String> getAncestorResourceURIs(boolean decode)
   {
      throw new RuntimeException("NOT IMPLEMENTED");
   }

   public List<String> getAncestorResourceURIs()
   {
      throw new RuntimeException("NOT IMPLEMENTED");
   }

   public List<Object> getAncestorResources()
   {
      throw new RuntimeException("NOT IMPLEMENTED");
   }
}
