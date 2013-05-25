package org.jboss.resteasy.core.registry;

import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.spi.HttpRequest;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RootNode
{
   protected SegmentNode root = new SegmentNode("");
   protected int size = 0;
   protected MultivaluedMap<String, MethodExpression> bounded = new MultivaluedHashMap<String, MethodExpression>();

   public int getSize()
   {
      return size;
   }

   public MultivaluedMap<String, ResourceInvoker> getBounded()
   {
      MultivaluedHashMap<String, ResourceInvoker> rtn = new MultivaluedHashMap<String, ResourceInvoker>();
      for (Map.Entry<String, List<MethodExpression>> entry : bounded.entrySet())
      {
         for (MethodExpression exp : entry.getValue())
         {
            rtn.add(entry.getKey(), exp.getInvoker());
         }
      }
      return rtn;
   }

   public ResourceInvoker match(HttpRequest request, int start)
   {
      return root.match(request, start);
   }

   public void removeBinding(String path, Method method)
   {
      List<MethodExpression> expressions = bounded.get(path);
      if (expressions == null) return;
      for (MethodExpression expression : expressions)
      {
         ResourceInvoker invoker = expression.getInvoker();
         if (invoker.getMethod().equals(method))
         {
            expression.parent.targets.remove(expression);
            expressions.remove(expression);
            if (expressions.size() == 0) bounded.remove(path);
            size--;
            if (invoker instanceof ResourceMethodInvoker)
            {
               ((ResourceMethodInvoker)invoker).cleanup();
            }
            return;
         }
      }
   }

   public void addInvoker(String path, ResourceInvoker invoker)
   {
      MethodExpression expression = addExpression(path, invoker);
      size++;
      bounded.add(path, expression);
   }
   protected MethodExpression addExpression(String path, ResourceInvoker invoker)
   {
      if (path.startsWith("/")) path = path.substring(1);
      if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
      if ("".equals(path))
      {
         if (invoker instanceof ResourceMethodInvoker)
         {
            MethodExpression expression = new MethodExpression(root, "", invoker);
            root.addExpression(expression);
            return expression;

         }
         else
         {
            MethodExpression expression = new MethodExpression(root, "", invoker, "(.*)");
            root.addExpression(expression);
            return expression;
         }
      }
      String expression = null;
      MethodExpression exp;
      //Matcher param = PathHelper.URI_PARAM_PATTERN.matcher(path);
      int expidx = path.indexOf('{');
      if (expidx > -1)
      {
         int i =expidx;
         while (i - 1 > -1)
         {
            if (path.charAt(i - 1) == '/')
            {
               break;
            }
            i--;
         }
         String staticPath = null;
         if (i > 0) staticPath = path.substring(0, i - 1);
         SegmentNode node = root;
         if (staticPath != null)
         {
            String[] split = staticPath.split("/");
            for (String segment : split)
            {
               SegmentNode tmp = node.children.get(segment);
               if (tmp == null)
               {
                  tmp = new SegmentNode(segment);
                  node.children.put(segment, tmp);
               }
               node = tmp;
            }
         }
         if (invoker instanceof ResourceMethodInvoker)
         {
            exp = new MethodExpression(node, path, invoker);
         }
         else
         {
            exp = new MethodExpression(node, path, invoker, "(/.+)?");

         }
         node.addExpression(exp);
      }
      else
      {
         String[] split = path.split("/");
         SegmentNode node = root;
         for (String segment : split)
         {
            SegmentNode tmp = node.children.get(segment);
            if (tmp == null)
            {
               tmp = new SegmentNode(segment);
               node.children.put(segment, tmp);
            }
            node = tmp;
         }
         if (invoker instanceof ResourceMethodInvoker)
         {
            exp = new MethodExpression(node, path, invoker);
            node.addExpression(exp);
         }
         else
         {
            exp = new MethodExpression(node, path, invoker, "(.*)");
            node.addExpression(exp);
         }
      }
      return exp;
   }
}
