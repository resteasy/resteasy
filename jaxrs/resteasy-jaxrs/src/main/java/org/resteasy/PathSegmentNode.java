package org.resteasy;

import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.util.HttpResponseCodes;
import org.resteasy.util.PathHelper;
import org.resteasy.util.SegmentInfo;
import org.resteasy.util.WeightedMediaType;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
   private Map<String, PathSegmentNode> children = new HashMap<String, PathSegmentNode>();
   private Map<String, PathSegmentNode> uriParamChildren = new HashMap<String, PathSegmentNode>();
   private List<PathSegmentNode> sortedUriParamChildren = new ArrayList<PathSegmentNode>();
   private SegmentInfo uriParamPattern;
   private boolean wildcard;


   private static class SortUriParamChild implements Comparator<PathSegmentNode>
   {
      public int compare(PathSegmentNode pathSegmentNode, PathSegmentNode pathSegmentNode1)
      {
         return pathSegmentNode.uriParamPattern.compareTo(pathSegmentNode1.uriParamPattern);
      }
   }

   public void addChild(String[] path, int pathIndex, ResourceMethod invoker, boolean wildcard)
   {
      String segment = path[pathIndex];
      Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(segment);
      if (matcher.find())
      {
         SegmentInfo info = new SegmentInfo(segment);
         PathSegmentNode child = uriParamChildren.get(info.getExpression());
         if (child == null)
         {
            child = new PathSegmentNode();
            uriParamChildren.put(info.getExpression(), child);
            child.uriParamPattern = info;
            sortedUriParamChildren.add(child);
            Collections.sort(sortedUriParamChildren, new SortUriParamChild());
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
      String segment = path[pathIndex];
      Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(segment);
      if (matcher.find())
      {
         SegmentInfo info = new SegmentInfo(segment);
         PathSegmentNode child = uriParamChildren.get(info.getExpression());
         if (child == null)
         {
            child = new PathSegmentNode();
            uriParamChildren.put(info.getExpression(), child);
            child.uriParamPattern = info;
            sortedUriParamChildren.add(child);
            Collections.sort(sortedUriParamChildren, new SortUriParamChild());
         }
         if (path.length == pathIndex + 1)
         {
            child.locator = locator;
            locator.setUriIndex((pathIndex + 1));
         }
         else
         {
            child.addChild(path, pathIndex + 1, locator);
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
      String segment = path[pathIndex];
      Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(path[pathIndex]);
      if (matcher.find())
      {
         String regex = PathHelper.createRegularExpressionFromPathExpression(segment);
         PathSegmentNode child = uriParamChildren.get(regex);
         if (child == null) return null;
         if (path.length == pathIndex + 1)
         {
            ResourceInvoker rm = tryRemoveInvoker(child.invokers, method);
            if (rm != null) return rm;
            return null;
         }
         else
         {
            ResourceInvoker rm = child.removeChild(path, pathIndex + 1, method);
            if (rm != null) return rm;
            return null;
         }
      }
      else
      {
         PathSegmentNode child = children.get(segment);
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
      String segment = path[pathIndex];
      Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(segment);
      if (matcher.matches())
      {
         String regex = PathHelper.createRegularExpressionFromPathExpression(segment);
         PathSegmentNode child = uriParamChildren.get(regex);
         if (path.length == pathIndex + 1)
         {
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
      if (pathIndex >= request.getPreProcessedSegments().size() || wildcard)
      {
         return match(request.getHttpMethod(), request.getHttpHeaders().getMediaType(), request.getHttpHeaders().getAcceptableMediaTypes());
      }
      else return findChild(request, response, pathIndex);
   }

   private ResourceInvoker findChild(HttpRequest request, HttpResponse response, int pathIndex)
   {
      List<PathSegment> path = request.getPreProcessedSegments();
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
      for (PathSegmentNode uriParamChild : sortedUriParamChildren)
      {
         if (!uriParamChild.uriParamPattern.getPattern().matcher(segment).matches()) continue;

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
