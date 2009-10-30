package org.jboss.resteasy.core;

import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.PathSegment;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
   private boolean pathSegment = false;
   private boolean pathSegmentArray = false;
   private boolean pathSegmentList = false;

   public PathParamInjector(Class type, Type genericType, AccessibleObject target, String paramName, String defaultValue, boolean encode, Annotation[] annotations, ResteasyProviderFactory factory)
   {
      this.type = type;
      if (isPathSegmentArray(type))
      {
         pathSegmentArray = true;
      }
      else if (isPathSegmentList(type, genericType))
      {
         pathSegmentList = true;
      }
      else if (type.equals(PathSegment.class))
      {
         pathSegment = true;
      }
      else
      {
         extractor = new StringParameterInjector(type, genericType, paramName, PathParam.class, defaultValue, target, annotations, factory);
      }
      this.paramName = paramName;
      this.encode = encode;
   }

   private boolean isPathSegmentArray(Class type)
   {
      return type.isArray() && type.getComponentType().equals(PathSegment.class);
   }

   private boolean isPathSegmentList(Class type, Type genericType)
   {
      Class collectionBaseType = Types.getCollectionBaseType(type, genericType);
      return List.class.equals(type) && collectionBaseType != null && collectionBaseType.equals(PathSegment.class);
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
         if (list == null)
         {
            throw new InternalServerErrorException("Unknown @PathParam: " + paramName + " for path: " + uriInfo.getPath());
         }
         PathSegment[] segments = list.get(list.size() - 1);
         if (pathSegmentArray)
         {
            return segments;
         }
         else if (pathSegmentList)
         {
            ArrayList<PathSegment> pathlist = new ArrayList<PathSegment>();
            for (PathSegment seg : segments)
            {
               pathlist.add(seg);
            }
            return pathlist;
         }
         else
         {
            return segments[segments.length - 1];
         }
      }
      else
      {
         List<String> list = request.getUri().getPathParameters(!encode).get(paramName);
         if (list == null)
         {
            throw new InternalServerErrorException("Unknown @PathParam: " + paramName + " for path: " + request.getUri().getPath());
         }
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
