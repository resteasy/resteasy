package org.jboss.resteasy.specimpl;

import org.jboss.resteasy.core.LoggerCategories;
import org.jboss.resteasy.util.Encode;
import org.jboss.resteasy.util.PathHelper;
import org.slf4j.Logger;

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
   private static final Logger logger = LoggerCategories.getSpecImplLogger();

   private String host;
   private String scheme;
   private int port = -1;

   // todo need to implement encoding
   private boolean encode = true;

   private String userInfo;
   private String path;
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
      impl.query = query;
      impl.fragment = fragment;

      return impl;
   }

   @Override
   public UriBuilder encode(boolean enable)
   {
      encode = enable;
      return this;
   }

   @Override
   public UriBuilder uri(URI uri) throws IllegalArgumentException
   {
      if (uri.getHost() != null) host = uri.getHost();
      if (uri.getScheme() != null) scheme = uri.getScheme();
      if (uri.getHost() != null) port = uri.getPort();
      if (uri.getUserInfo() != null) userInfo = uri.getRawUserInfo();
      if (uri.getPath() != null && !uri.getPath().equals("")) path = uri.getRawPath();
      if (uri.getFragment() != null) fragment = uri.getRawFragment();
      if (uri.getQuery() != null) query = uri.getRawQuery();
      return this;
   }

   @Override
   public UriBuilder scheme(String scheme) throws IllegalArgumentException
   {
      this.scheme = scheme;
      return this;
   }

   @Override
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

   @Override
   public UriBuilder userInfo(String ui) throws IllegalArgumentException
   {
      this.userInfo = ui;
      return this;
   }

   @Override
   public UriBuilder host(String host) throws IllegalArgumentException
   {
      this.host = host;
      return this;
   }

   @Override
   public UriBuilder port(int port) throws IllegalArgumentException
   {
      this.port = port;
      return this;
   }

   @Override
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

   @Override
   public UriBuilder path(String... segments) throws IllegalArgumentException
   {
      path = paths(isEncode(), path, segments);
      return this;
   }

   @Override
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

   @Override
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

   @Override
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

   @Override
   public UriBuilder replaceMatrixParams(String matrix) throws IllegalArgumentException
   {

      if (!matrix.startsWith(";")) matrix = ";" + matrix;
      if (path == null)
      {
         path = matrix;
      }
      else
      {
         int start = path.lastIndexOf('/');
         if (start < 0) start = 0;
         int matrixIndex = path.indexOf(';', start);
         if (matrixIndex > -1) path = path.substring(0, matrixIndex) + matrix;
         else path += matrix;

      }
      return this;
   }

   @Override
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

   @Override
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


   @Override
   public URI build() throws UriBuilderException
   {
      return build(path);
   }

   protected URI build(String tmpPath) throws UriBuilderException
   {
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

   @Override
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

   @Override
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

   @Override
   public String getExtension()
   {
      return null;
   }

   @Override
   public UriBuilder matrixParam(String name, Object... values) throws IllegalArgumentException
   {
      if (path == null) path = "";
      for (Object val : values)
      {
         path += ";" + encodeSegment(name) + "=" + encodeSegment(val.toString());
      }
      return this;
   }

   @Override
   public UriBuilder replaceMatrixParam(String name, Object... values) throws IllegalArgumentException
   {
      if (path == null) return matrixParam(name, values);
      int start = path.lastIndexOf('/');
      if (start < 0) start = 0;
      int matrixIndex = path.indexOf(';', start);
      if (matrixIndex > -1) return matrixParam(name, values);

      String matrixParams = path.substring(matrixIndex + 1);
      path = path.substring(0, matrixIndex);
      MultivaluedMapImpl<String, String> map = new MultivaluedMapImpl<String, String>();

      String[] params = matrixParams.split(";");
      for (String param : params)
      {
         String[] namevalue = param.split("=");
         if (namevalue != null && namevalue.length > 0)
         {
            String theName = namevalue[0];
            String value = "";
            if (namevalue.length > 1)
            {
               value = namevalue[1];
            }
            map.add(theName, value);
         }
      }
      map.remove(name);
      for (String theName : map.keySet())
      {
         List<String> vals = map.get(theName);
         for (Object val : vals)
         {
            path += ";" + name + "=" + val.toString();
         }
      }
      return matrixParam(name, values);
   }

   public UriBuilder queryParam(String name, String value) throws IllegalArgumentException
   {
      if (query == null) query = encodeString(name) + "=" + encodeString(value);
      else query += "&" + encodeString(name) + "=" + encodeString(value);
      return this;
   }

   @Override
   public UriBuilder queryParam(String name, Object... values) throws IllegalArgumentException
   {

      for (Object value : values)
      {
         if (query == null) query = "";
         else query += "&";
         query += encodeString(name) + "=" + encodeString(value.toString());
      }
      return this;
   }

   @Override
   public UriBuilder replaceQueryParam(String name, Object... values) throws IllegalArgumentException
   {
      if (query == null || query.equals("")) return queryParam(name, values);

      String[] params = query.split("&");
      query = null;

      String replacedName = encodeString(name);

      for (String param : params)
      {
         if (param.indexOf('=') >= 0)
         {
            String[] nv = param.split("=");
            String paramName = nv[0];
            if (paramName.equals(replacedName)) continue;

            if (query == null) query = "";
            else query += "&";
            query += nv[0] + "=" + nv[1];
         }
         else
         {
            if (param.equals(replacedName)) continue;

            if (query == null) query = "";
            else query += "&";
            query += param;
         }
      }
      return queryParam(name, values);
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

   @Override
   public UriBuilder extension(String extension)
   {
      if (path != null)
      {
         int lastPath = path.lastIndexOf('/');
         if (lastPath < 0) lastPath = 0;
         int index = path.indexOf('.', lastPath);
         if (index > -1) path = path.substring(0, index);
      }
      if (extension == null) return this;

      if (extension.startsWith(".")) extension = extension.substring(1);
      if (isEncode()) extension = Encode.encodeSegment(extension);
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

   public static void main(String[] args) throws Exception
   {
      String path = "/foo.txt/hello.html";

      path = removeDot(path);

      logger.trace(path);

      path = "foo/bar";

      path = removeDot(path);

      logger.trace(path);


   }

   private static String removeDot(String path)
   {
      int lastPath = path.lastIndexOf('/');
      if (lastPath < 0) lastPath = 0;
      int index = path.indexOf('.', lastPath);
      if (index > -1) path = path.substring(0, index);
      return path;
   }
}
