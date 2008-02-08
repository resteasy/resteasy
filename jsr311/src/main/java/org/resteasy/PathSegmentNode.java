package org.resteasy;

import org.resteasy.util.MediaTypeHelper;
import org.resteasy.util.PathHelper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
   private List<PathSegmentNode> uriParamChildren = new ArrayList<PathSegmentNode>();
   private Map<String, PathSegmentNode> children = new HashMap<String, PathSegmentNode>();
   private PathSegmentNode wildcard;

   public PathSegmentNode()
   {
   }

   public void addChild(String[] path, int pathIndex, ResourceMethod invoker)
   {
      Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(path[pathIndex]);
      if (matcher.matches())
      {
         PathSegmentNode child = new PathSegmentNode();
         uriParamChildren.add(child);
         if (path.length == pathIndex + 1)
         {
            child.invokers.add(invoker);
         }
         else
         {
            child.addChild(path, ++pathIndex, invoker);
         }
      }
      else if (path[pathIndex].trim().equals("*"))
      {
         if (wildcard == null) wildcard = new PathSegmentNode();
         wildcard.invokers.add(invoker);
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
            child.invokers.add(invoker);
         }
         else
         {
            child.addChild(path, ++pathIndex, invoker);
         }
      }
   }

   public ResourceMethod removeChild(String[] path, int pathIndex, Method method)
   {
      Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(path[pathIndex]);
      if (matcher.matches())
      {
         if (path.length == pathIndex + 1)
         {
            for (PathSegmentNode child : uriParamChildren)
            {
               ResourceMethod rm = tryRemoveInvoker(child.invokers, method);
               if (rm != null) return rm;
            }
            return null;
         }
         else
         {
            for (PathSegmentNode child : uriParamChildren)
            {
               ResourceMethod rm = child.removeChild(path, ++pathIndex, method);
               if (rm != null) return rm;
            }
            return null;
         }
      }
      else if (path[pathIndex].trim().equals("*"))
      {
         if (wildcard == null) return null;
         return tryRemoveInvoker(wildcard.invokers, method);
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
            return child.removeChild(path, ++pathIndex, method);
         }
      }
   }

   private ResourceMethod tryRemoveInvoker(List<ResourceMethod> invokers, Method method)
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

   public ResourceMethod findResourceInvoker(String httpMethod, List<PathSegment> path, int pathIndex, MediaType contentType, List<MediaType> accepts)
   {
      if (pathIndex >= path.size()) return match(httpMethod, contentType, accepts);
      else return findChild(httpMethod, path, pathIndex, contentType, accepts);
   }

   private ResourceMethod findChild(String httpMethod, List<PathSegment> path, int pathIndex, MediaType contentType, List<MediaType> accepts)
   {
      PathSegmentNode next = children.get(path.get(pathIndex).getPath());
      if (next != null)
      {
         ResourceMethod method = next.findResourceInvoker(httpMethod, path, ++pathIndex, contentType, accepts);
         if (method != null) return method;
      }
      if (uriParamChildren != null)
      {
         for (PathSegmentNode wildcard : uriParamChildren)
         {
            ResourceMethod wildcardReturn = wildcard.findResourceInvoker(httpMethod, path, ++pathIndex, contentType, accepts);
            if (wildcardReturn != null) return wildcardReturn;
         }
      }
      if (wildcard != null)
      {
         return wildcard.match(httpMethod, contentType, accepts);
      }
      return null;
   }

   private ResourceMethod match(String httpMethod, MediaType contentType, List<MediaType> accepts)
   {
      List<ResourceMethod> list = new ArrayList<ResourceMethod>();
      IdentityHashMap<MediaType, ResourceMethod> consumesMap = new IdentityHashMap<MediaType, ResourceMethod>();

      // make a list of all compatible ResourceMethods
      // Populate the consumes identity map with media types from each ResourceMethod
      for (ResourceMethod invoker : invokers)
      {
         if (invoker.matchByType(contentType, accepts) && invoker.getHttpMethods().contains(httpMethod))
         {
            list.add(invoker);
            if (invoker.getConsumes() == null)
            {
               MediaType defaultConsumes = MediaType.parse("*/*;q=0.0");
               consumesMap.put(defaultConsumes, invoker);
            }
            else
            {
               for (MediaType consume : invoker.getConsumes())
               {
                  consumesMap.put(consume, invoker);
               }
            }
         }
      }

      if (list.size() == 0) return null;
      if (list.size() == 1) return list.get(0);

      list = new ArrayList<ResourceMethod>();
      ArrayList<MediaType> consumes = new ArrayList<MediaType>();
      consumes.addAll(consumesMap.keySet());
      MediaTypeHelper.sort(consumes);

      boolean first = true;
      MediaType current = null;

      // pull out top choices that have equal weighting
      for (MediaType type : consumes)
      {
         if (first)
         {
            list.add(consumesMap.get(type));
            current = type;
            first = false;
         }
         else
         {
            if (MediaTypeHelper.same(current, type))
            {
               list.add(consumesMap.get(type));
            }
            else break;
         }
      }

      if (list.size() == 1) return list.get(0);

      // make an identiy map of produced media types
      IdentityHashMap<MediaType, ResourceMethod> producesMap = new IdentityHashMap<MediaType, ResourceMethod>();
      for (ResourceMethod invoker : list)
      {
         if (invoker.getProduces() == null)
         {
            MediaType defaultProduces = MediaType.parse("*/*;q=0.0");
            producesMap.put(defaultProduces, invoker);
         }
         else
         {
            for (MediaType produce : invoker.getProduces())
            {
               producesMap.put(produce, invoker);
            }
         }
      }

      // sort media types then get first in list and match it into identity map
      ArrayList<MediaType> produces = new ArrayList<MediaType>();
      produces.addAll(producesMap.keySet());
      MediaTypeHelper.sort(produces);
      MediaTypeHelper.sort(accepts);

      for (MediaType accept : accepts)
      {
         for (MediaType produce : produces)
         {
            if (accept.isCompatible(produce)) return producesMap.get(produce);
         }

      }
      return null;
   }

}
