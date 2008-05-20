package org.resteasy.specimpl;

import org.resteasy.util.Encode;
import org.resteasy.util.PathHelper;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import java.lang.reflect.Method;
import java.net.URI;
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
   private boolean encode = true;

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
      if (uri.getUserInfo() != null) userInfo = uri.getRawUserInfo();
      if (uri.getPath() != null && !uri.getPath().equals("")) path = uri.getRawPath();
      if (path != null)
      {
         int idx = path.indexOf(';');
         if (idx > -1)
         {
            matrix = path.substring(idx);
            path = path.substring(0, idx);
         }
      }
      if (uri.getFragment() != null) fragment = uri.getRawFragment();
      if (uri.getQuery() != null) query = uri.getRawQuery();
      return this;
   }

   public UriBuilder scheme(String scheme) throws IllegalArgumentException
   {
      this.scheme = scheme;
      return this;
   }

   public UriBuilder schemeSpecificPart(String ssp) throws IllegalArgumentException
   {
      StringBuffer uriStr = new StringBuffer();
      if (scheme != null) uriStr.append(scheme).append(":");
      uriStr.append(ssp);
      if (fragment != null)
      {
         uriStr.append("#").append(fragment);
      }
      return uri(URI.create(uriStr.toString()));
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
      this.path = paths(isEncode(), null, segments);
      return this;
   }

   protected static String paths(boolean encode, String basePath, String... segments)
   {
      String path = basePath;
      if (path == null) path = "";
      for (String segment : segments)
      {
         if ("".equals(segment)) continue;
         if (!path.endsWith("/")) path += "/";
         if (segment.equals("/")) continue;
         if (segment.startsWith("/")) segment = segment.substring(1);
         if (encode) segment = Encode.encodePath(segment);
         path += segment;

      }
      return path;
   }

   public UriBuilder path(String... segments) throws IllegalArgumentException
   {
      path = paths(isEncode(), path, segments);
      return this;
   }

   public UriBuilder path(Class resource) throws IllegalArgumentException
   {
      Path ann = (Path) resource.getAnnotation(Path.class);
      if (ann != null)
      {
         String[] segments = new String[]{ann.value()};
         path = paths(ann.encode(), path, segments);
      }
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
         if (ann != null)
         {
            String[] segments = new String[]{ann.value()};
            path = paths(ann.encode(), path, segments);
         }
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

   protected String encodeString(String value)
   {
      if (!isEncode()) return value;
      return Encode.encodeSegment(value);
   }

   public UriBuilder queryParam(String name, String value) throws IllegalArgumentException
   {
      if (query == null) query = encodeString(name) + "=" + encodeString(value);
      else query += "&" + encodeString(name) + "=" + encodeString(value);
      return this;
   }

   public UriBuilder fragment(String fragment) throws IllegalArgumentException
   {
      this.fragment = encodeString(fragment);
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
      if (matrix != null)
      {
         if (tmpPath == null) tmpPath = "";
         if (!matrix.startsWith(";")) tmpPath += ";";
         tmpPath += matrix;
      }
      StringBuffer buffer = new StringBuffer();
      if (scheme != null) buffer.append(scheme).append("://");
      if (userInfo != null) buffer.append(userInfo).append("@");
      if (host != null) buffer.append(host);
      if (port != -1 && port != 80) buffer.append(":").append(Integer.toString(port));
      if (tmpPath != null) buffer.append(tmpPath);
      if (query != null) buffer.append("?").append(query);
      if (fragment != null) buffer.append("#").append(fragment);
      String buf = buffer.toString();
      try
      {
         return URI.create(buf);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Failed to create URI: " + buf, e);
      }
   }


   private String encodeSegment(String value)
   {
      if (isEncode()) return Encode.encodeSegment(value);
      return value;
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
            paths[i] = encodeSegment(value.toString());
         }
         i++;
      }
      String tmpPath = paths(isEncode(), null, paths);
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
      if (isEncode() && path != null) this.path = Encode.encodePath(path);
      else this.path = path;
   }

   public UriBuilder extension(String extension)
   {
      if (isEncode()) Encode.encodeSegment(extension);
      if (path == null)
      {
         path = "." + extension;
      }
      else
      {
         if (!path.endsWith(".")) path += ".";
         path += extension;
      }
      return this;
   }
}
