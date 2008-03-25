package org.resteasy;

import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;

import javax.ws.rs.QueryParam;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueryParamInjector extends StringParameterInjector implements ValueInjector
{


   public QueryParamInjector(Class type, Type genericType, AccessibleObject target, String paramName, String defaultValue)
   {
      super(type, genericType, paramName, "@" + QueryParam.class.getSimpleName(), defaultValue, target);
   }

   public Object inject(HttpRequest request, HttpResponse response)
   {
      List<String> list = request.getUri().getQueryParameters().get(paramName);
      return extractValues(list);
   }

   public Object inject()
   {
      throw new RuntimeException("It is illegal to inject a @QueryParam into a singleton");
   }


}
