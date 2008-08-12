package org.jboss.resteasy.core;

import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

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
   private StringParameterInjector extractor;
   private String paramName;
   private boolean encode;

   public PathParamInjector(Class type, Type genericType, AccessibleObject target, String paramName, String defaultValue, boolean encode)
   {
      if (type.equals(PathSegment.class) == false)
      {
         extractor = new StringParameterInjector(type, genericType, paramName, "@" + PathParam.class.getSimpleName(), defaultValue, target);
      }
      this.paramName = paramName;
      this.encode = encode;
   }

   public Object inject(HttpRequest request, HttpResponse response)
   {
      if (extractor == null) // we are a PathSegment
      {
         UriInfoImpl uriInfo = (UriInfoImpl) request.getUri();
         if (encode)
         {
            List<PathSegment> list = uriInfo.getEncodedPathParameterPathSegments().get(paramName);
            return list.get(list.size() - 1);
         }
         else
         {
            List<PathSegment> list = uriInfo.getPathParameterPathSegments().get(paramName);
            return list.get(list.size() - 1);
         }
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
