package org.jboss.resteasy.core.registry;

import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceLocatorInvoker;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.LoggableFailure;
import org.jboss.resteasy.util.PathHelper;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RootSegment extends Segment
{
   protected Map<String, SimpleSegment> simpleSegments = new HashMap<String, SimpleSegment>();
   protected Map<String, PathParamSegment> resourceExpressions = new HashMap<String, PathParamSegment>();
   protected List<PathParamSegment> sortedResourceExpressions = new ArrayList<PathParamSegment>();
   protected Map<String, PathParamSegment> locatorExpressions = new HashMap<String, PathParamSegment>();
   protected List<PathParamSegment> sortedLocatorExpressions = new ArrayList<PathParamSegment>();
   protected Map<String, List<ResourceInvoker>> bounded = new LinkedHashMap<String, List<ResourceInvoker>>();

   /**
    * Return a map of paths and what resource methods they are bound to
    *
    * @return
    */
   public Map<String, List<ResourceInvoker>> getBounded()
   {
      return bounded;
   }

   @Override
   protected boolean isEmpty()
   {
      return super.isEmpty() && simpleSegments.size() == 0 && resourceExpressions.size() == 0 && locatorExpressions.size() == 0;
   }

   protected void addPath(String[] segments, int index, ResourceInvoker invoker)
   {
      String segment = segments[index];
      // Regular expressions can have '{' and '}' characters.  Replace them to do match
      String replacedCurlySegment = PathHelper.replaceEnclosedCurlyBraces(segment);
      Matcher withPathParam = PathHelper.URI_PARAM_PATTERN.matcher(replacedCurlySegment);
      if (withPathParam.find())
      {
         String expression = recombineSegments(segments, index);

         if (invoker instanceof ResourceLocatorInvoker)
         {
            PathParamSegment segmentNode = locatorExpressions.get(expression);
            if (segmentNode != null)
               throw new LoggableFailure("You cannot have 2 locators for same path: " + expression);
            segmentNode = new PathParamSegment(expression);
            segmentNode.locator = (ResourceLocatorInvoker) invoker;
            locatorExpressions.put(segmentNode.getPathExpression(), segmentNode);
            sortedLocatorExpressions.add(segmentNode);
            Collections.sort(sortedLocatorExpressions);
         }
         else
         {
            PathParamSegment segmentNode = resourceExpressions.get(expression);
            if (segmentNode == null)
            {
               segmentNode = new PathParamSegment(expression);
               resourceExpressions.put(segmentNode.getPathExpression(), segmentNode);
               sortedResourceExpressions.add(segmentNode);
               Collections.sort(sortedResourceExpressions);
            }
            segmentNode.methods.add((ResourceMethodInvoker) invoker);
         }
      }
      else
      {
         SimpleSegment segmentNode = simpleSegments.get(segment);
         if (segmentNode == null)
         {
            segmentNode = new SimpleSegment(segment);
            simpleSegments.put(segment, segmentNode);
         }
         if (segments.length > index + 1)
         {
            segmentNode.addPath(segments, index + 1, invoker);
         }
         else
         {
            if (invoker instanceof ResourceLocatorInvoker)
            {
               segmentNode.locator = (ResourceLocatorInvoker) invoker;
            }
            else
            {
               segmentNode.methods.add((ResourceMethodInvoker) invoker);
            }
         }
      }

   }

   private String recombineSegments(String[] segments, int index)
   {
      String expression = "";
      boolean first = true;
      for (int i = index; i < segments.length; i++)
      {
         if (first)
         {
            first = false;
         }
         else
         {
            expression += "/";
         }
         expression += segments[i];
      }
      return expression;
   }

   protected ResourceInvoker removePath(String[] segments, int index, Method method)
   {
      String segment = segments[index];
      Matcher withPathParam = PathHelper.URI_PARAM_PATTERN.matcher(segment);
      if (withPathParam.find())
      {
         String expression = recombineSegments(segments, index);
         PathParamSegment locatorSegment = locatorExpressions.get(expression);
         if (locatorSegment != null && locatorSegment.locator != null && locatorSegment.locator.getMethod().equals(method))
         {
            PathParamSegment rtn = locatorExpressions.remove(expression);
            if (rtn != null)
            {
               sortedLocatorExpressions.remove(rtn);
               return rtn.locator;
            }
            else return null;
         }
         else
         {
            PathParamSegment node = resourceExpressions.get(expression);
            if (node == null) return null;
            Iterator<ResourceMethodInvoker> it = node.methods.iterator();
            try
            {
               while (it.hasNext())
               {
                  ResourceMethodInvoker invoker = it.next();
                  if (invoker.getMethod().equals(method))
                  {
                     it.remove();
                     return invoker;
                  }

               }
            }
            finally
            {
               if (node.isEmpty())
               {
                  PathParamSegment seg = resourceExpressions.remove(expression);
                  if (seg != null) sortedResourceExpressions.remove(seg);
               }
            }
         }
      }
      else
      {
         SimpleSegment segmentNode = simpleSegments.get(segment);
         if (segmentNode == null)
         {
            return null;
         }
         if (segments.length > index + 1)
         {
            try
            {
               return segmentNode.removePath(segments, index + 1, method);
            }
            finally
            {
               if (segmentNode.isEmpty()) simpleSegments.remove(segment);
            }
         }
         else
         {
            try
            {
               if (segmentNode.locator != null && method.equals(segmentNode.locator.getMethod()))
               {
                  ResourceLocatorInvoker loc = segmentNode.locator;
                  segmentNode.locator = null;
                  return loc;
               }
               else
               {
                  Iterator<ResourceMethodInvoker> it = segmentNode.methods.iterator();
                  while (it.hasNext())
                  {
                     ResourceMethodInvoker invoker = it.next();
                     if (invoker.getMethod().equals(method))
                     {
                        it.remove();
                        return invoker;
                     }

                  }
               }
            }
            finally
            {
               if (segmentNode.isEmpty()) simpleSegments.remove(segment);
            }
         }
      }
      return null;

   }


   public void addPath(String path, ResourceInvoker invoker)
   {
      List<ResourceInvoker> list = bounded.get(path);
      if (list == null)
      {
         list = new ArrayList<ResourceInvoker>();
         bounded.put(path, list);
      }
      list.add(invoker);
      if (path.startsWith("/")) path = path.substring(1);

      MultivaluedMapImpl<String, String> pathParamExpr = new MultivaluedMapImpl<String, String>();
      StringBuffer newPath = pullPathParamExpressions(path, pathParamExpr);
      path = newPath.toString();
      String[] segments = path.split("/");

      for (int i = 0; i < segments.length; i++)
      {
         segments[i] = putBackPathParamExpressions(segments[i], pathParamExpr);
      }
      addPath(segments, 0, invoker);
   }

   public ResourceInvoker removePath(String path, Method method)
   {
      List<ResourceInvoker> list = bounded.get(path);
      if (list != null)
      {
         ResourceInvoker removed = null;
         for (ResourceInvoker invoker : list)
         {
            if (invoker instanceof ResourceMethodInvoker)
            {
               ResourceMethodInvoker rm = (ResourceMethodInvoker) invoker;
               if (rm.getMethod().equals(method))
               {
                  removed = rm;
                  break;
               }
            }
            else
            {
               ResourceLocatorInvoker locator = (ResourceLocatorInvoker) invoker;
               if (locator.getMethod().equals(method))
               {
                  removed = locator;
                  break;
               }

            }
         }
         list.remove(removed);
         if (list.size() == 0) bounded.remove(path);
      }
      if (path.startsWith("/")) path = path.substring(1);

      MultivaluedMapImpl<String, String> pathParamExpr = new MultivaluedMapImpl<String, String>();
      StringBuffer newPath = pullPathParamExpressions(path, pathParamExpr);
      path = newPath.toString();
      String[] segments = path.split("/");

      for (int i = 0; i < segments.length; i++)
      {
         segments[i] = putBackPathParamExpressions(segments[i], pathParamExpr);
      }
      return removePath(segments, 0, method);
   }

   protected ResourceInvoker matchChildren(HttpRequest request, String path, int start)
   {
      String simpleSegment = null;
      if (start == path.length())
      {
         simpleSegment = "";
      }
      else
      {
         int endOfSegmentIndex = path.indexOf('/', start);
         if (endOfSegmentIndex > -1) simpleSegment = path.substring(start, endOfSegmentIndex);
         else simpleSegment = path.substring(start);
      }

      RuntimeException lastException = null;

      SimpleSegment segment = simpleSegments.get(simpleSegment);
      if (segment != null)
      {
         try
         {
            return segment.matchSimple(request, path, start);
         }
         catch (RuntimeException e)
         {
            lastException = e;
         }
      }

      for (PathParamSegment pathParamSegment : sortedResourceExpressions)
      {
         try
         {
            return pathParamSegment.matchPattern(request, path, start);
         }
         catch (RuntimeException e)
         {
            // try and propagate matched path that threw non-404 responses, i.e. MethodNotAllowed, etc.
            if (lastException == null || lastException instanceof NotFoundException) lastException = e;
         }
      }
      for (PathParamSegment pathParamSegment : sortedLocatorExpressions)
      {
         try
         {
            return pathParamSegment.matchPattern(request, path, start);
         }
         catch (RuntimeException e)
         {
            // try and propagate matched path that threw non-404 responses, i.e. MethodNotAllowed, etc.
            if (lastException == null || lastException instanceof NotFoundException) lastException = e;
         }
      }
      if (lastException != null) throw lastException;
      throw new NotFoundException("Could not find resource for relative : " + path + " of full path: " + request.getUri().getRequestUri());
   }


   public ResourceInvoker matchRoot(HttpRequest request)
   {
      int start = 0;
      return matchRoot(request, start);
   }

   public ResourceInvoker matchRoot(HttpRequest request, int start)
   {
      String path = request.getUri().getMatchingPath();
      if (start < path.length() && path.charAt(start) == '/') start++;
      return matchChildren(request, path, start);
   }


   private static StringBuffer pullPathParamExpressions(String path, MultivaluedMapImpl<String, String> pathParamExpr)
   {
      // Regular expressions can have '{' and '}' characters.  Replace them to do match
      path = PathHelper.replaceEnclosedCurlyBraces(path);

      Matcher matcher = PathHelper.URI_PARAM_WITH_REGEX_PATTERN.matcher(path);
      StringBuffer newPath = new StringBuffer();
      while (matcher.find())
      {
         String name = matcher.group(1);
         String regex = matcher.group(3);
         // Regular expressions can have '{' and '}' characters.  Recover original replacement
         pathParamExpr.add(name, PathHelper.recoverEnclosedCurlyBraces(regex));
         matcher.appendReplacement(newPath, "{$1:x}");
      }
      matcher.appendTail(newPath);
      return newPath;
   }

   private static String putBackPathParamExpressions(String path, MultivaluedMapImpl<String, String> pathParamExpr)
   {
      Matcher matcher = PathHelper.URI_PARAM_WITH_REGEX_PATTERN.matcher(path);
      StringBuffer newPath = new StringBuffer();
      while (matcher.find())
      {
         String name = matcher.group(1);
         String val = pathParamExpr.get(name).remove(0);
         // double encode slashes, so that slashes stay where they are 
         val = val.replace("\\", "\\\\");
         val = val.replace("$", "\\$");
         matcher.appendReplacement(newPath, "{$1:" + val + "}");
      }
      matcher.appendTail(newPath);
      return newPath.toString();
   }

}
