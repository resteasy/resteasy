package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

import javax.ws.rs.HeaderParam;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HeaderParamInjector extends StringParameterInjector implements ValueInjector
{

   public HeaderParamInjector(Class type, Type genericType, AccessibleObject target, String header, String defaultValue)
   {
      super(type, genericType, header, "@" + HeaderParam.class.getSimpleName(), defaultValue, target);
   }

   public Object inject(HttpRequest request, HttpResponse response)
   {
      List<String> list = request.getHttpHeaders().getRequestHeaders().get(paramName);
      return extractValues(list);
   }

   public Object inject()
   {
      throw new RuntimeException("It is illegal to inject a @HeaderParam into a singleton");
   }
}
