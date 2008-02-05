package org.resteasy;

import org.resteasy.util.PathHelper;

import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
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

   public ResourceMethod findResourceInvoker(String httpMethod, String[] path, int pathIndex, MediaType contentType, List<MediaType> accepts)
   {
      if (pathIndex >= path.length) return match(httpMethod, contentType, accepts);
      else return findChild(httpMethod, path, pathIndex, contentType, accepts);
   }

   private ResourceMethod findChild(String httpMethod, String[] path, int pathIndex, MediaType contentType, List<MediaType> accepts)
   {
      PathSegmentNode next = children.get(path[pathIndex]);
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
      for (ResourceMethod invoker : invokers)
      {
         if (invoker.matchByType(contentType, accepts) && invoker.getHttpMethods().contains(httpMethod))
            return invoker;
      }
      return null;
   }


}
