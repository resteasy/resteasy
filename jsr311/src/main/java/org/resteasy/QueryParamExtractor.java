package org.resteasy;

import org.resteasy.spi.HttpInput;

import javax.ws.rs.QueryParam;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueryParamExtractor extends StringParameterExtractor implements ParameterExtractor
{


   public QueryParamExtractor(Method method, String paramName, int index, String defaultValue)
   {
      super(index, method, paramName, "@" + QueryParam.class.getSimpleName(), defaultValue);
   }

   public Object extract(HttpInput request)
   {
      List<String> list = request.getParameters().get(paramName);
      return extractValues(list);
   }

}
