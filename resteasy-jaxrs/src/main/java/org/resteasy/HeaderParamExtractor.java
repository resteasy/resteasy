package org.resteasy;

import org.resteasy.spi.HttpRequest;

import javax.ws.rs.HeaderParam;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HeaderParamExtractor extends StringParameterExtractor implements ParameterExtractor
{

   public HeaderParamExtractor(Class type, Type genericType, AccessibleObject target, String header, String defaultValue)
   {
      super(type, genericType, header, "@" + HeaderParam.class.getSimpleName(), defaultValue, target);
   }

   public Object extract(HttpRequest request)
   {
      List<String> list = request.getHttpHeaders().getRequestHeaders().get(paramName);
      return extractValues(list);
   }
}
