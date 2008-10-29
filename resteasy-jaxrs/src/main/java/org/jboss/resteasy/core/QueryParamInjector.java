package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.LoggableFailure;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.QueryParam;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueryParamInjector extends StringParameterInjector implements ValueInjector
{
   private boolean encode;
   private String encodedName;

   public QueryParamInjector(Class type, Type genericType, AccessibleObject target, String paramName, String defaultValue, boolean encode, ResteasyProviderFactory factory)
   {
      super(type, genericType, paramName, QueryParam.class, defaultValue, target, factory);
      this.encode = encode;
      try
      {
         this.encodedName = URLDecoder.decode(paramName, "UTF-8");
      }
      catch (UnsupportedEncodingException e)
      {
         throw new LoggableFailure(e);
      }
   }

   public Object inject(HttpRequest request, HttpResponse response)
   {
      if (encode)
      {
         List<String> list = request.getUri().getQueryParameters(false).get(encodedName);
         return extractValues(list);
      }
      else
      {
         List<String> list = request.getUri().getQueryParameters().get(paramName);
         return extractValues(list);

      }
   }

   public Object inject()
   {
      throw new RuntimeException("It is illegal to inject a @QueryParam into a singleton");
   }


}
