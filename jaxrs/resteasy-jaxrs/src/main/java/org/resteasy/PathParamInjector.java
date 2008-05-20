package org.resteasy;

import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.PathSegment;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PathParamInjector implements ValueInjector
{
   private PathParamIndex index;
   private StringParameterInjector extractor;
   private String paramName;
   private boolean encode;

   public PathParamInjector(PathParamIndex index, Class type, Type genericType, AccessibleObject target, String paramName, String defaultValue, boolean encode)
   {
      if (type.equals(PathSegment.class) == false)
      {
         extractor = new StringParameterInjector(type, genericType, paramName, "@" + PathParam.class.getSimpleName(), defaultValue, target);
      }
      this.paramName = paramName;
      this.index = index;
      this.encode = encode;
   }

   public Object inject(HttpRequest request, HttpResponse response)
   {
      if (extractor == null) // we are a PathSegment
      {
         List<Integer> list = index.getUriParams().get(paramName);
         return request.getUri().getPathSegments(!encode).get(list.get(list.size() - 1));
      }
      else
      {
         List<String> list = request.getUri().getPathParameters(!encode).get(paramName);
         return extractor.extractValue(list.get(list.size() - 1));
      }
   }

   public Object inject()
   {
      throw new RuntimeException("It is illegal to inject a @PathParam into a singleton");
   }

}
