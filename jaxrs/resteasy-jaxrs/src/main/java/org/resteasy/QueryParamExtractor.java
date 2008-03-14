package org.resteasy;

import org.resteasy.spi.HttpInput;

import javax.ws.rs.QueryParam;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueryParamExtractor extends StringParameterExtractor implements ParameterExtractor
{


   public QueryParamExtractor(Class type, Type genericType, AccessibleObject target, String paramName, String defaultValue)
   {
      super(type, genericType, paramName, "@" + QueryParam.class.getSimpleName(), defaultValue, target);
   }

   public Object extract(HttpInput request)
   {
      List<String> list = request.getParameters().get(paramName);
      return extractValues(list);
   }

}
