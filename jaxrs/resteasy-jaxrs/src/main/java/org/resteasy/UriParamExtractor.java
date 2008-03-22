package org.resteasy;

import org.resteasy.spi.HttpRequest;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.PathSegment;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
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

   public UriParamExtractor(ResourceInvoker invoker, Class type, Type genericType, AccessibleObject target, String paramName, String defaultValue)
   {
      if (type.equals(PathSegment.class) == false)
      {
         extractor = new StringParameterExtractor(type, genericType, paramName, "@" + PathParam.class.getSimpleName(), defaultValue, target);
      }
      this.paramName = paramName;
      this.invoker = invoker;
   }

   public Object extract(HttpRequest request)
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
