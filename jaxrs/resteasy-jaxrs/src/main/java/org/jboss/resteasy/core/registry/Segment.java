package org.jboss.resteasy.core.registry;

import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceLocatorInvoker;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.spi.DefaultOptionsMethodException;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.MethodNotAllowedException;
import org.jboss.resteasy.spi.NotAcceptableException;
import org.jboss.resteasy.spi.NotFoundException;
import org.jboss.resteasy.spi.UnsupportedMediaTypeException;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.util.WeightedMediaType;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Segment
{
   public static final String RESTEASY_CHOSEN_ACCEPT = "RESTEASY_CHOSEN_ACCEPT";
   protected List<ResourceMethodInvoker> methods = new ArrayList<ResourceMethodInvoker>();
   protected ResourceLocatorInvoker locator;

   protected boolean isEmpty()
   {
      return methods.size() == 0 && locator == null;
   }

   /**
    * @param httpMethod this is so we can find a GET match when HEAD is called (and there is no head)
    * @param request
    * @return
    */
   protected ResourceInvoker match(String httpMethod, HttpRequest request)
   {
      MediaType contentType = request.getHttpHeaders().getMediaType();

      List<MediaType> oldaccepts = request.getHttpHeaders().getAcceptableMediaTypes();
      List<WeightedMediaType> accepts = new ArrayList<WeightedMediaType>();
      for (MediaType accept : oldaccepts) accepts.add(WeightedMediaType.parse(accept));

      List<ResourceMethodInvoker> list = new ArrayList<ResourceMethodInvoker>();

      boolean methodMatch = false;
      boolean consumeMatch = false;

      // make a list of all compatible ResourceMethods
      for (ResourceMethodInvoker invoker : methods)
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
               }
            }

         }
      }

      if (list.size() == 0)
      {
         if (locator != null) return locator;
         if (methods == null || methods.size() == 0)
         {
            throw new NotFoundException("Could not find resource for full path: " + request.getUri().getRequestUri());
         }
         if (!methodMatch)
         {
            HashSet<String> allowed = new HashSet<String>();
            for (ResourceMethodInvoker invoker : methods) allowed.addAll(invoker.getHttpMethods());

            if (httpMethod.equalsIgnoreCase("HEAD") && allowed.contains("GET"))
            {
               return match("GET", request);
            }

            if (allowed.contains("GET")) allowed.add("HEAD");
            allowed.add("OPTIONS");
            String allowHeaderValue = "";
            boolean first = true;
            for (String allow : allowed)
            {
               if (first) first = false;
               else allowHeaderValue += ", ";
               allowHeaderValue += allow;
            }

            if (httpMethod.equals("OPTIONS"))
            {
               Response res = Response.ok().header(HttpHeaderNames.ALLOW, allowHeaderValue).build();
               throw new DefaultOptionsMethodException("No resource method found for options, return OK with Allow header", res);
            }
            else
            {
               Response res = Response.status(HttpResponseCodes.SC_METHOD_NOT_ALLOWED).header(HttpHeaderNames.ALLOW, allowHeaderValue).build();
               throw new MethodNotAllowedException("No resource method found for " + httpMethod + ", return 405 with Allow header", res);
            }
         }
         else if (!consumeMatch)
         {
            throw new UnsupportedMediaTypeException("Cannot consume content type");
         }
         throw new NotAcceptableException("No match for accept header");
      }
      if (list.size() == 1) return list.get(0);

      // Populate the consumes identity map with media types from each ResourceMethod
      // so that we can easily pick invokers after media types are sorted
      IdentityHashMap<WeightedMediaType, ResourceMethodInvoker> consumesMap = new IdentityHashMap<WeightedMediaType, ResourceMethodInvoker>();
      for (ResourceMethodInvoker invoker : list)
      {
         if (invoker.getConsumes() == null || invoker.getConsumes().length == 0)
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

      list = new ArrayList<ResourceMethodInvoker>();
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
      IdentityHashMap<WeightedMediaType, ResourceMethodInvoker> producesMap = new IdentityHashMap<WeightedMediaType, ResourceMethodInvoker>();
      for (ResourceMethodInvoker invoker : list)
      {
         if (invoker.getProduces() == null || invoker.getProduces().length == 0)
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
            if (accept.isCompatible(produce))
            {
               //System.out.println("SEGMENT: " + accept + " accepts: " + accepts);
               request.setAttribute(RESTEASY_CHOSEN_ACCEPT, accept);
               return producesMap.get(produce);
            }
         }

      }


      /*
      I'm keeping this code around as its a different way to match.  It takes producer preference
      over picking accept type listed first.


      // make a list of lists for each level of preferences

      List<List<WeightedMediaType>> acceptsLevels = new ArrayList<List<WeightedMediaType>>();

      WeightedMediaType last = null;
      List<WeightedMediaType> level = null;
      for (WeightedMediaType accept : accepts)
      {
         if (last == null || accept.getWeight() < last.getWeight())
         {
            level = new ArrayList<WeightedMediaType>();
            acceptsLevels.add(level);
            level.add(accept);

         }
         else
         {
            level.add(accept);
         }
         last = accept;
      }

      // preferred produces are matched first with preferred accepts.
      for (List<WeightedMediaType> acceptLevel : acceptsLevels)
      {
         for (WeightedMediaType produce : produces)
         {
            for (WeightedMediaType accept : acceptLevel)
            {
               if (accept.isCompatible(produce))
               {
                  // this is a big hack to propagate chosen accept type to ServerResponse
                  request.setAttribute(RESTEASY_CHOSEN_ACCEPT, accept);
                  return producesMap.get(produce);
               }
            }
         }
      }
       */

      return null;
   }

}
