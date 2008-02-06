package org.resteasy;

import org.resteasy.spi.HttpInput;
import org.resteasy.spi.ResourceFactory;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.util.FindAnnotation;
import org.resteasy.util.PathHelper;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpContext;
import javax.ws.rs.core.UriInfo;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class ResourceInvoker
{
   protected ResourceFactory factory;
   protected ResteasyProviderFactory providerFactory;
   protected Method method;
   protected ParameterExtractor[] params;
   protected Map<Integer, String> uriParams = new HashMap<Integer, String>();
   protected String path;

   public ResourceInvoker(String path, ResourceFactory factory, Method method, ResteasyProviderFactory providerFactory)
   {
      this.factory = factory;
      this.method = method;
      this.providerFactory = providerFactory;
      params = new ParameterExtractor[method.getParameterTypes().length];
      this.path = path;
      if (path.startsWith("/")) path = path.substring(1);
      String[] paths = path.split("/");
      int i = 0;
      for (String p : paths)
      {
         Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(p);
         if (matcher.matches())
         {
            String uriParamName = matcher.group(2);
            uriParams.put(i, uriParamName);
         }
         i++;
      }
      for (i = 0; i < method.getParameterTypes().length; i++)
      {
         Class type = method.getParameterTypes()[i];
         Annotation[] annotations = method.getParameterAnnotations()[i];

         DefaultValue defaultValue = FindAnnotation.findAnnotation(method.getParameterAnnotations()[i], DefaultValue.class);
         String defaultVal = null;
         if (defaultValue != null) defaultVal = defaultValue.value();

         QueryParam query;
         HeaderParam header;
         MatrixParam matrix;
         PathParam uriParam;

         if ((query = FindAnnotation.findAnnotation(annotations, QueryParam.class)) != null)
         {
            params[i] = new QueryParamExtractor(method, query.value(), i, defaultVal);
         }
         else if ((header = FindAnnotation.findAnnotation(annotations, HeaderParam.class)) != null)
         {
            params[i] = new HeaderParamExtractor(method, header.value(), i, defaultVal);
         }
         else if ((uriParam = FindAnnotation.findAnnotation(annotations, PathParam.class)) != null)
         {
            params[i] = new UriParamExtractor(method, uriParam.value(), i, defaultVal);
         }
         else if ((matrix = FindAnnotation.findAnnotation(annotations, MatrixParam.class)) != null)
         {
            params[i] = new MatrixParamExtractor(method, matrix.value(), i, defaultVal);
         }
         else if (FindAnnotation.findAnnotation(annotations, HttpContext.class) != null)
         {
            params[i] = new HttpContextParameter(type);
         }
         else
         {
            params[i] = new MessageBodyParameterExtractor(type, providerFactory);
         }
      }
   }

   protected Object[] getArguments(HttpInput input)
   {
      Object[] args = null;
      if (params != null && params.length > 0)
      {
         args = new Object[params.length];
         int i = 0;
         for (ParameterExtractor extractor : params)
         {
            args[i++] = extractor.extract(input);
         }
      }
      return args;
   }

   protected void populateUriParams(HttpInput input)
   {
      UriInfo uriInfo = input.getUri();
      for (int i : uriParams.keySet())
      {
         String paramName = uriParams.get(i);
         String value = uriInfo.getPathSegments().get(i).getPath();

         // put single so that we override any Locator's that set the uriParams to be what we don't want
         uriInfo.getTemplateParameters().putSingle(paramName, value);
      }
   }

   public Method getMethod()
   {
      return method;
   }
}
