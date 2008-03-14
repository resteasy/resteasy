package org.resteasy.specimpl;

import org.resteasy.util.PathHelper;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UriBuilderImpl extends UriBuilder
{
   private String host;
   private String scheme;
   private int port = -1;

   // todo need to implement encoding
   private boolean encode;

   private String userInfo;
   private String path;
   private String matrix;
   private String query;
   private String fragment;


   public UriBuilder clone()
   {
      UriBuilderImpl impl = new UriBuilderImpl();
      impl.host = host;
      impl.scheme = scheme;
      impl.port = port;
      impl.encode = encode;
      impl.userInfo = userInfo;
      impl.path = path;
      impl.matrix = matrix;
      impl.query = query;
      impl.fragment = fragment;

      return impl;
   }

   public UriBuilder encode(boolean enable)
   {
      encode = enable;
      return this;
   }

   public UriBuilder uri(URI uri) throws IllegalArgumentException
   {
      if (uri.getHost() != null) host = uri.getHost();
      if (uri.getScheme() != null) scheme = uri.getScheme();
      if (uri.getHost() != null) port = uri.getPort();
      if (uri.getUserInfo() != null) userInfo = uri.getUserInfo();
      if (uri.getPath() != null && !uri.getPath().equals("")) path = uri.getPath();
      if (path != null)
      {
         int idx = path.indexOf(';');
         if (idx > -1)
         {
            matrix = path.substring(idx);
            path = path.substring(0, idx);
         }
      }
      if (uri.getFragment() != null) fragment = uri.getFragment();
      if (uri.getQuery() != null) query = uri.getQuery();
      return this;
   }

   public UriBuilder scheme(String scheme) throws IllegalArgumentException
   {
      this.scheme = scheme;
      return this;
   }

   public UriBuilder schemeSpecificPart(String ssp) throws IllegalArgumentException
   {
      URI uri = null;
      try
      {
         uri = new URI(scheme, ssp, fragment);
      }
      catch (URISyntaxException e)
      {
         throw new RuntimeException(e);
      }
      return uri(uri);
   }

   public UriBuilder userInfo(String ui) throws IllegalArgumentException
   {
      this.userInfo = ui;
      return this;
   }

   public UriBuilder host(String host) throws IllegalArgumentException
   {
      this.host = host;
      return this;
   }

   public UriBuilder port(int port) throws IllegalArgumentException
   {
      this.port = port;
      return this;
   }

   public UriBuilder replacePath(String... segments) throws IllegalArgumentException
   {
      this.path = paths(null, segments);
      return this;
   }

   protected static String paths(String basePath, String... segments)
   {
      String path = basePath;
      if (path == null) path = "";
      for (String segment : segments)
      {
         if ("".equals(segment)) continue;
         if (!path.endsWith("/")) path += "/";
         if (segment.equals("/")) continue;
         if (segment.startsWith("/")) segment = segment.substring(1);
         path += segment;
      }
      return path;
   }

   public UriBuilder path(String... segments) throws IllegalArgumentException
   {
      path = paths(path, segments);
      return this;
   }

   public UriBuilder path(Class resource) throws IllegalArgumentException
   {
      Path ann = (Path) resource.getAnnotation(Path.class);
      if (ann != null) path(ann.value());
      return this;
   }

   public UriBuilder path(Class resource, String method) throws IllegalArgumentException
   {
      for (Method m : resource.getMethods())
      {
         if (m.getName().equals(method))
         {
            return path(m);
         }
      }
      return this;
   }

   public UriBuilder path(Method... methods) throws IllegalArgumentException
   {
      for (Method method : methods)
      {
         Path ann = method.getAnnotation(Path.class);
         if (ann != null) path(ann.value());
      }
      return this;
   }

   public UriBuilder replaceMatrixParams(String matrix) throws IllegalArgumentException
   {
      this.matrix = matrix;
      return this;
   }

   public UriBuilder matrixParam(String name, String value) throws IllegalArgumentException
   {
      if (this.matrix == null) matrix = "";
      StringBuilder tmp = new StringBuilder(this.matrix);
      tmp.append(";").append(name).append("=").append(value);
      matrix = tmp.toString();
      return this;
   }

   public UriBuilder replaceQueryParams(String query) throws IllegalArgumentException
   {
      this.query = query;
      return this;
   }

   public UriBuilder queryParam(String name, String value) throws IllegalArgumentException
   {
      if (query == null) query = name + "=" + value;
      else query += "&" + name + "=" + value;
      return this;
   }

   public UriBuilder fragment(String fragment) throws IllegalArgumentException
   {
      this.fragment = fragment;
      return this;
   }

   /**
    * Replace first found uri parameter of name with give value
    *
    * @param name
    * @param value
    * @return
    * @throws IllegalArgumentException if name or value is null or
    *                                  if automatic encoding is disabled the paramter value contains illegal characters
    */
   public UriBuilder uriParam(String name, String value) throws IllegalArgumentException
   {
      if (path == null) return this;
      if (path.startsWith("/")) path = path.substring(1);
      String[] paths = path.split("/");
      int i = 0;
      for (String p : paths)
      {
         Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(p);
         if (matcher.matches())
         {
            String uriParamName = matcher.group(2);
            if (uriParamName.equals(name))
            {
               paths[i] = value;
               break;
            }
         }
         i++;
      }
      path = null;
      path(paths);
      return this;
   }


   public URI build() throws UriBuilderException
   {
      return build(path);
   }

   protected URI build(String tmpPath) throws UriBuilderException
   {
      try
      {
         if (matrix != null)
         {
            if (!matrix.startsWith(";")) tmpPath += ";";
            tmpPath += matrix;
         }
         return new URI(scheme, userInfo, host, port, tmpPath, query, fragment);
      }
      catch (URISyntaxException e)
      {
         throw new UriBuilderException(e);
      }
   }


   public URI build(Map<String, Object> values) throws IllegalArgumentException, UriBuilderException
   {
      if (values.size() <= 0 || path == null) return build();
      if (path.startsWith("/")) path = path.substring(1);
      String[] paths = path.split("/");
      int i = 0;
      for (String p : paths)
      {
         Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(p);
         if (matcher.matches())
         {
            String uriParamName = matcher.group(2);
            Object value = values.get(uriParamName);
            if (value == null)
               throw new IllegalArgumentException("uri parameter {" + uriParamName + "} does not exist as a value");
            paths[i] = value.toString();
         }
         i++;
      }
      String tmpPath = paths(null, paths);
      return build(tmpPath);
   }

   protected List<String> getUriParamNamesInDeclarationOrder()
   {
      List<String> params = new ArrayList<String>();
      if (path == null) return params;
      if (path.startsWith("/")) path = path.substring(1);
      String[] paths = path.split("/");
      for (String p : paths)
      {
         Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(p);
         if (matcher.matches())
         {
            params.add(matcher.group(2));
         }
      }
      return params;
   }

   public URI build(Object... values) throws IllegalArgumentException, UriBuilderException
   {
      if (values.length <= 0) return build();
      List<String> params = getUriParamNamesInDeclarationOrder();
      if (params.size() == 0) throw new IllegalArgumentException("There are no @PathParams");

      Map<String, Object> pathParams = new HashMap<String, Object>();

      int i = 0;

      for (Object val : values)
      {
         String pathParam = params.get(i++);
         if (pathParams.containsKey(pathParam))
            throw new IllegalArgumentException("More values passed in than there are @PathParams");
         pathParams.put(pathParam, val.toString());
      }
      return build(pathParams);
   }


   public String getHost()
   {
      return host;
   }

   public String getScheme()
   {
      return scheme;
   }

   public int getPort()
   {
      return port;
   }

   public boolean isEncode()
   {
      return encode;
   }

   public String getUserInfo()
   {
      return userInfo;
   }

   public String getPath()
   {
      return path;
   }

   public String getMatrix()
   {
      return matrix;
   }

   public String getQuery()
   {
      return query;
   }

   public String getFragment()
   {
      return fragment;
   }

   /**
    * nullable
    */
   public void setPath(String path)
   {
      this.path = path;
   }
}
