package org.resteasy;

import org.resteasy.spi.HttpInput;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.PathSegment;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UriParamExtractor implements ParameterExtractor
{
   private ResourceInvoker invoker;
   private StringParameterExtractor extractor;
   private String paramName;

   public UriParamExtractor(ResourceInvoker invoker, Method method, String paramName, int index, String defaultValue)
   {
      if (method.getParameterTypes()[index].equals(PathSegment.class) == false)
      {
         extractor = new StringParameterExtractor(index, method, paramName, "@" + PathParam.class.getSimpleName(), defaultValue);
      }
      this.paramName = paramName;
      this.invoker = invoker;
   }

   public Object extract(HttpInput request)
   {
      if (extractor == null) // we are a PathSegment
      {
         List<Integer> list = invoker.getUriParams().get(paramName);
         return request.getUri().getPathSegments().get(list.get(list.size() - 1));
      }
      else
      {
         List<String> list = request.getUri().getTemplateParameters().get(paramName);
         return extractor.extractValue(list.get(list.size() - 1));
      }
   }
}
