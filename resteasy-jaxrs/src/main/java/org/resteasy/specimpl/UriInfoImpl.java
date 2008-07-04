package org.jboss.resteasy.specimpl;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
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
   private List<String> ancestorUris;
   private List<String> encodedAncestorUris;
   private List<Object> ancestors;


   /**
    * @param absolutePath
    * @param path         decoded equivalent to HttpServletRequest.getPathInfo()
    * @param queryString  encoded query string of request
    */
   public UriInfoImpl(URI absolutePath, String path, String queryString)
   {
      this(absolutePath, path, queryString, PathSegmentImpl.parseSegments(path));
   }

   public UriInfoImpl(URI absolutePath, String encodedPath, String queryString, List<PathSegment> encodedPathSegments)
   {
      this.encodedPath = encodedPath;
      try
      {
         this.path = URLDecoder.decode(encodedPath, "UTF-8");
      }
      catch (UnsupportedEncodingException e)
      {
         throw new RuntimeException(e);
      }
      //System.out.println("path: " + path);
      //System.out.println("encodedPath: " + encodedPath);
      this.absolutePath = absolutePath;
      this.queryParameters = new MultivaluedMapImpl<String, String>();
      this.encodedQueryParameters = new MultivaluedMapImpl<String, String>();
      extractParameters(queryString);
      this.encodedTemplateParameters = new MultivaluedMapImpl<String, String>();
      this.templateParameters = new MultivaluedMapImpl<String, String>();
      this.encodedPathSegments = encodedPathSegments;
      this.pathSegments = new ArrayList<PathSegment>(encodedPathSegments.size());
      for (PathSegment segment : encodedPathSegments)
      {
         try
         {
            pathSegments.add(new PathSegmentImpl(URLDecoder.decode(((PathSegmentImpl) segment).getOriginal(), "UTF-8")));
         }
         catch (UnsupportedEncodingException e)
         {
            throw new RuntimeException(e);
         }
      }


      this.queryString = queryString;


      if (queryString == null) this.absolutePathWithQueryString = absolutePath;
      else
      {
         this.absolutePathWithQueryString = URI.create(absolutePath.toString() + "?" + queryString);
      }
      if (encodedPath.trim().equals("")) baseURI = absolutePath;
      else
      {
         String abs = absolutePath.getPath();
         abs = abs.substring(0, abs.indexOf(encodedPath));
         if (!abs.endsWith("/")) abs += "/";
//         System.out.println("abs: " + abs);
//         System.out.println("absolutePath: " + absolutePath);
//         System.out.println("encodedPath: " + encodedPath);
         try
         {
            baseURI = UriBuilder.fromUri(absolutePath).encode(false).replacePath(abs).build();

         }
         catch (Exception e)
         {
            throw new RuntimeException("URI value was: " + abs + " encodedPath: " + encodedPath, e);
         }
      }
   }

   // this is here for our TESTSUITE, do not invoke or use this method
   public UriInfoImpl(List<PathSegment> pathSegments)
   {
      this.pathSegments = pathSegments;
      this.encodedPathSegments = pathSegments;
   }

   public UriInfoImpl clone()
   {
      return new UriInfoImpl(absolutePath, encodedPath, queryString, encodedPathSegments);
   }

   public String getPath()
   {
      return path;
   }

   public String getPath(boolean decode)
   {
      if (decode) return getPath();
      return encodedPath;
   }

   public List<PathSegment> getPathSegments()
   {
      if (pathSegments != null) return pathSegments;
      pathSegments = PathSegmentImpl.parseSegments(getPath());
      return pathSegments;
   }

   public List<PathSegment> getPathSegments(boolean decode)
   {
      if (decode) return getPathSegments();
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

   public void addEncodedPathParameter(String name, String value)
   {
      encodedTemplateParameters.add(name, value);
      try
      {
         templateParameters.add(name, URLDecoder.decode(value, "UTF-8"));
      }
      catch (UnsupportedEncodingException e)
      {
         throw new RuntimeException(e);
      }
   }

   public MultivaluedMap<String, String> getPathParameters(boolean decode)
   {
      if (decode) return getPathParameters();
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
      if (decode)
      {
         if (ancestorUris == null) ancestorUris = new ArrayList<String>();
         return ancestorUris;
      }
      else
      {
         if (encodedAncestorUris == null) encodedAncestorUris = new ArrayList<String>();
         return ancestorUris;
      }
   }

   public List<String> getAncestorResourceURIs()
   {
      return getAncestorResourceURIs(true);
   }

   public List<Object> getAncestorResources()
   {
      if (ancestors == null) ancestors = new ArrayList<Object>();
      return ancestors;
   }


   public void pushCurrentResource(Object resource)
   {
      if (ancestors == null) ancestors = new ArrayList<Object>();
      ancestors.add(0, resource);
   }

   public void popCurrentResource()
   {
      if (ancestors != null && ancestors.size() > 0)
      {
         ancestors.remove(0);
      }
   }

   public void pushAncestorURI(String encoded, String decoded)
   {
      if (encodedAncestorUris == null) encodedAncestorUris = new ArrayList<String>();
      encodedAncestorUris.add(0, encoded);

      if (ancestorUris == null) ancestorUris = new ArrayList<String>();
      ancestorUris.add(0, decoded);
   }

   public void popAncestorURI()
   {
      if (encodedAncestorUris != null && encodedAncestorUris.size() > 0)
      {
         encodedAncestorUris.remove(0);
      }
      if (ancestorUris != null && ancestorUris.size() > 0)
      {
         ancestorUris.remove(0);
      }
   }
}
