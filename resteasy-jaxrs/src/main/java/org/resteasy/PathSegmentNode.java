package org.resteasy;

import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.util.HttpResponseCodes;
import org.resteasy.util.PathHelper;
import org.resteasy.util.WeightedMediaType;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PathSegmentNode
{
   private List<ResourceMethod> invokers = new ArrayList<ResourceMethod>();
   private ResourceLocator locator;
   private PathSegmentNode uriParamChild;
   private Map<String, PathSegmentNode> children = new HashMap<String, PathSegmentNode>();
   private boolean wildcard;

   public PathSegmentNode()
   {
   }

   public void addChild(String[] path, int pathIndex, ResourceMethod invoker, boolean wildcard)
   {
      String segment = path[pathIndex];
      Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(segment);
      if (matcher.matches())
      {
         if (uriParamChild == null)
         {
            uriParamChild = new PathSegmentNode();
         }
         if (path.length == pathIndex + 1)
         {
            uriParamChild.invokers.add(invoker);
            uriParamChild.wildcard = wildcard;
         }
         else
         {
            uriParamChild.addChild(path, pathIndex + 1, invoker, wildcard);
         }
      }
      else
      {
         PathSegmentNode child = children.get(segment);
         if (child == null)
         {
            child = new PathSegmentNode();
            children.put(segment, child);
         }
         if (path.length == pathIndex + 1)
         {
            child.invokers.add(invoker);
            child.wildcard = wildcard;
         }
         else
         {
            child.addChild(path, pathIndex + 1, invoker, wildcard);
         }
      }
   }

   public void addChild(String[] path, int pathIndex, ResourceLocator locator)
   {
      Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(path[pathIndex]);
      if (matcher.matches())
      {
         if (uriParamChild == null)
         {
            uriParamChild = new PathSegmentNode();
         }
         if (path.length == pathIndex + 1)
         {
            uriParamChild.locator = locator;
            locator.setUriIndex(pathIndex + 1);
         }
         else
         {
            uriParamChild.addChild(path, pathIndex + 1, locator);
         }
      }
      else
      {
         PathSegmentNode child = children.get(path[pathIndex]);
         if (child == null)
         {
            child = new PathSegmentNode();
            children.put(path[pathIndex], child);
         }
         if (path.length == pathIndex + 1)
         {
            child.locator = locator;
            locator.setUriIndex(pathIndex + 1);
         }
         else
         {
            child.addChild(path, pathIndex + 1, locator);
         }
      }
   }

   public ResourceInvoker removeChild(String[] path, int pathIndex, Method method)
   {
      Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(path[pathIndex]);
      if (matcher.matches())
      {
         if (path.length == pathIndex + 1)
         {
            ResourceInvoker rm = tryRemoveInvoker(uriParamChild.invokers, method);
            if (rm != null) return rm;
            return null;
         }
         else
         {
            ResourceInvoker rm = uriParamChild.removeChild(path, pathIndex + 1, method);
            if (rm != null) return rm;
            return null;
         }
      }
      else
      {
         PathSegmentNode child = children.get(path[pathIndex]);
         if (path.length == pathIndex + 1)
         {
            return tryRemoveInvoker(child.invokers, method);
         }
         else
         {
            return child.removeChild(path, pathIndex + 1, method);
         }
      }
   }

   public ResourceInvoker removeLocator(String[] path, int pathIndex)
   {
      Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(path[pathIndex]);
      if (matcher.matches())
      {
         if (path.length == pathIndex + 1)
         {
            PathSegmentNode child = uriParamChild;
            if (child == null) return null;
            if (child.locator != null)
            {
               ResourceInvoker invoker = child.locator;
               child.locator = null;
               return invoker;
            }
            return null;
         }
         else
         {
            PathSegmentNode child = uriParamChild;
            if (child == null) return null;
            ResourceInvoker rm = child.removeLocator(path, pathIndex + 1);
            if (rm != null) return rm;
            return null;
         }
      }
      else
      {
         PathSegmentNode child = children.get(path[pathIndex]);
         if (path.length == pathIndex + 1)
         {
            ResourceInvoker invoker = child.locator;
            child.locator = null;
            return invoker;
         }
         else
         {
            return child.removeLocator(path, pathIndex + 1);
         }
      }
   }

   private ResourceInvoker tryRemoveInvoker(List<ResourceMethod> invokers, Method method)
   {
      List<ResourceMethod> copy = new ArrayList<ResourceMethod>();
      copy.addAll(invokers);
      for (ResourceMethod rm : copy)
      {
         if (method.equals(rm.getMethod())) invokers.remove(rm);
         return rm;
      }
      return null;
   }

   public PathSegmentNode getChild(String segment)
   {
      return children.get(segment);
   }

   public ResourceInvoker findResourceInvoker(HttpRequest request, HttpResponse response, int pathIndex)
   {
      if (pathIndex >= request.getUri().getPathSegments().size() || wildcard)
      {
         return match(request.getHttpMethod(), request.getHttpHeaders().getMediaType(), request.getHttpHeaders().getAcceptableMediaTypes());
      }
      else return findChild(request, response, pathIndex);
   }

   private ResourceInvoker findChild(HttpRequest request, HttpResponse response, int pathIndex)
   {
      List<PathSegment> path = request.getUri().getPathSegments();
      String segment = path.get(pathIndex).getPath();
      PathSegmentNode next = children.get(segment);
      Failure failure = null;
      if (next != null)
      {
         try
         {
            if (next.wildcard)
            {
               return next.match(request.getHttpMethod(), request.getHttpHeaders().getMediaType(), request.getHttpHeaders().getAcceptableMediaTypes());
            }
            else
            {
               ResourceInvoker method = next.findResourceInvoker(request, response, pathIndex + 1);
               if (method != null) return method;
            }
         }
         catch (Failure e)
         {
            failure = e;
            // special case for root path, there should be no other way to have an empty ""
            if (path.get(pathIndex).getPath().equals("")) throw failure;
         }
      }
      if (uriParamChild != null)
      {
         try
         {
            if (uriParamChild.wildcard)
            {
               return uriParamChild.match(request.getHttpMethod(), request.getHttpHeaders().getMediaType(), request.getHttpHeaders().getAcceptableMediaTypes());

            }
            else
            {
               ResourceInvoker result = uriParamChild.findResourceInvoker(request, response, pathIndex + 1);
               if (result != null) return result;
            }
         }
         catch (Failure e)
         {
            failure = e;
         }
      }
      if (locator != null) return locator;
      if (failure != null) throw failure;
      return null;
   }

   private ResourceInvoker match(String httpMethod, MediaType contentType, List<MediaType> oldaccepts)
   {
      List<WeightedMediaType> accepts = new ArrayList<WeightedMediaType>();
      for (MediaType accept : oldaccepts) accepts.add(WeightedMediaType.parse(accept));

      List<ResourceMethod> list = new ArrayList<ResourceMethod>();
      IdentityHashMap<WeightedMediaType, ResourceMethod> consumesMap = new IdentityHashMap<WeightedMediaType, ResourceMethod>();

      boolean methodMatch = false;
      boolean consumeMatch = false;

      // make a list of all compatible ResourceMethods
      // Populate the consumes identity map with media types from each ResourceMethod
      for (ResourceMethod invoker : invokers)
      {

         if (invoker.getHttpMethods().contains(httpMethod))
         {
            methodMatch = true;
            if (invoker.doesConsume(contentType))
            {
               consumeMatch = true;
               if (invoker.doesProduce(accepts))
               {
                  list.add(invoker);
                  if (invoker.getConsumes() == null)
                  {
                     WeightedMediaType defaultConsumes = WeightedMediaType.valueOf("*/*;q=0.0");
                     consumesMap.put(defaultConsumes, invoker);
                  }
                  else
                  {
                     for (WeightedMediaType consume : invoker.getPreferredConsumes())
                     {
                        consumesMap.put(consume, invoker);
                     }
                  }
               }
            }

         }
      }

      if (list.size() == 0)
      {
         if (locator != null) return locator;
         if (!methodMatch)
         {
            throw new Failure("No matching http method", HttpResponseCodes.SC_METHOD_NOT_ALLOWED);
         }
         if (!consumeMatch)
         {
            throw new Failure("Cannot consume content type", HttpResponseCodes.SC_UNSUPPORTED_MEDIA_TYPE);
         }
         throw new Failure("No match for accept header", HttpResponseCodes.SC_NOT_ACCEPTABLE);
      }
      if (list.size() == 1) return list.get(0);

      list = new ArrayList<ResourceMethod>();
      ArrayList<WeightedMediaType> consumes = new ArrayList<WeightedMediaType>();
      consumes.addAll(consumesMap.keySet());
      Collections.sort(consumes);

      boolean first = true;
      WeightedMediaType current = null;

      // pull out top choices that have equal weighting and that are the same
      for (WeightedMediaType type : consumes)
      {
         if (first)
         {
            list.add(consumesMap.get(type));
            current = type;
            first = false;
         }
         else
         {
            if (current.compareTo(type) == 0)
            {
               list.add(consumesMap.get(type));
            }
            else break;
         }
      }

      if (list.size() == 1) return list.get(0);

      // make an identiy map of produced media types
      IdentityHashMap<WeightedMediaType, ResourceMethod> producesMap = new IdentityHashMap<WeightedMediaType, ResourceMethod>();
      for (ResourceMethod invoker : list)
      {
         if (invoker.getProduces() == null)
         {
            WeightedMediaType defaultProduces = WeightedMediaType.valueOf("*/*;q=0.0");
            producesMap.put(defaultProduces, invoker);
         }
         else
         {
            for (WeightedMediaType produce : invoker.getPreferredProduces())
            {
               producesMap.put(produce, invoker);
            }
         }
      }

      if (accepts == null || accepts.size() == 0)
      {
         accepts = new ArrayList<WeightedMediaType>(1);
         accepts.add(WeightedMediaType.valueOf("*/*"));
      }
      // sort media types then get first in list and match it into identity map
      ArrayList<WeightedMediaType> produces = new ArrayList<WeightedMediaType>();
      produces.addAll(producesMap.keySet());
      Collections.sort(produces);
      Collections.sort(accepts);

      for (WeightedMediaType accept : accepts)
      {
         for (WeightedMediaType produce : produces)
         {
            if (accept.isCompatible(produce)) return producesMap.get(produce);
         }

      }
      return null;
   }
}
