package org.jboss.resteasy.core.registry;

import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.spi.HttpRequest;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RootClassNode
{
   protected ClassNode root = new ClassNode("");
   protected Map<String, ClassExpression> bounded = new HashMap<String, org.jboss.resteasy.core.registry.ClassExpression>();
   public int getSize()
   {
      int size = 0;
      for (ClassExpression exp : bounded.values())
      {
         size += exp.getRoot().getSize();
      }
      return size;
   }

   public Map<String, List<ResourceInvoker>> getBounded()
   {
      MultivaluedMap<String, ResourceInvoker> invokers = new MultivaluedHashMap<String, ResourceInvoker>();
      for (ClassExpression exp : bounded.values())
      {
         MultivaluedMap<String, ResourceInvoker> expBounded = exp.getRoot().getBounded();
         for (MultivaluedMap.Entry<String, List<ResourceInvoker>> entry : expBounded.entrySet())
         {
            invokers.addAll(entry.getKey(), entry.getValue());
         }
      }
      return invokers;
   }

   public ResourceInvoker match(HttpRequest request, int start)
   {
      return root.match(request, start).match(request, start);
   }

   public void removeBinding(String classExpression, String path, Method method)
   {
      if (classExpression.startsWith("/")) classExpression = classExpression.substring(1);
      if (classExpression.endsWith("/")) classExpression = classExpression.substring(0, classExpression.length() - 1);
      String regex = new ClassExpression(classExpression).getRegex();
      ClassExpression ce = bounded.get(regex);
      if (ce == null) return;
      ce.getRoot().removeBinding(path, method);
      if (ce.getRoot().getSize() == 0)
      {
         ce.getParent().targets.remove(ce);
         bounded.remove(regex);
      }
   }

   public void addInvoker(String classExpression, String fullpath, ResourceInvoker invoker)
   {
      if (classExpression.startsWith("/")) classExpression = classExpression.substring(1);
      if (classExpression.endsWith("/")) classExpression = classExpression.substring(0, classExpression.length() - 1);
      ClassExpression newce = new ClassExpression(classExpression);
      String regex = newce.getRegex();
      ClassExpression existing = bounded.get(regex);
      if (existing == null)
      {
         newce.getRoot().addInvoker(fullpath, invoker);
         addExpression(classExpression, newce);
         bounded.put(regex, newce);
      }
      else
      {
         existing.getRoot().addInvoker(fullpath, invoker);
      }
   }
   protected void addExpression(String path, ClassExpression ce)
   {
      if (path.startsWith("/")) path = path.substring(1);
      if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
      if ("".equals(path))
      {
         ce.parent = root;
         root.targets.add(ce);
         return;
      }
      String expression = null;
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
         ClassNode node = root;
         if (staticPath != null)
         {
            String[] split = staticPath.split("/");
            for (String segment : split)
            {
               ClassNode tmp = node.children.get(segment);
               if (tmp == null)
               {
                  tmp = new ClassNode(segment);
                  node.children.put(segment, tmp);
               }
               node = tmp;
            }
         }
         ce.parent = node;
         node.targets.add(ce);
      }
      else
      {
         String[] split = path.split("/");
         ClassNode node = root;
         for (String segment : split)
         {
            ClassNode tmp = node.children.get(segment);
            if (tmp == null)
            {
               tmp = new ClassNode(segment);
               node.children.put(segment, tmp);
            }
            node = tmp;
         }
         ce.parent = node;
         node.targets.add(ce);
      }
   }
}
