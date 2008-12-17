package org.jboss.resteasy.core;

import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

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
   private Class type;

   public PathParamInjector(Class type, Type genericType, AccessibleObject target, String paramName, String defaultValue, boolean encode, ResteasyProviderFactory factory)
   {
      this.type = type;
      if (type.equals(PathSegment.class) == false && !(isPathSegmentArray(type)))
      {
         extractor = new StringParameterInjector(type, genericType, paramName, PathParam.class, defaultValue, target, factory);
      }
      this.paramName = paramName;
      this.encode = encode;
   }

   private boolean isPathSegmentArray(Class type)
   {
      return type.isArray() && type.getComponentType().equals(PathSegment.class);
   }

   public Object inject(HttpRequest request, HttpResponse response)
   {
      if (extractor == null) // we are a PathSegment
      {
         UriInfoImpl uriInfo = (UriInfoImpl) request.getUri();
         List<PathSegment[]> list = null;
         if (encode)
         {
            list = uriInfo.getEncodedPathParameterPathSegments().get(paramName);
         }
         else
         {
            list = uriInfo.getPathParameterPathSegments().get(paramName);
         }
         PathSegment[] segments = list.get(list.size() - 1);
         if (isPathSegmentArray(type))
         {
            return segments;
         }
         else
         {
            return segments[segments.length - 1];
         }
      }
      else
      {
         List<String> list = request.getUri().getPathParameters(!encode).get(paramName);
         if (extractor.isCollectionOrArray())
         {
            return extractor.extractValues(list);
         }
         else
         {
            return extractor.extractValue(list.get(list.size() - 1));
         }
      }
   }

   public Object inject()
   {
      throw new RuntimeException("It is illegal to inject a @PathParam into a singleton");
   }

}
