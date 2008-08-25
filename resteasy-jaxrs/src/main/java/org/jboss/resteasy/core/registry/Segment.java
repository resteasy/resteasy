package org.jboss.resteasy.core.registry;

import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceLocator;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.spi.Failure;
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
   protected List<ResourceMethod> methods = new ArrayList<ResourceMethod>();
   protected ResourceLocator locator;

   protected boolean isEmpty()
   {
      return methods.size() == 0 && locator == null;
   }

   protected ResourceInvoker match(String httpMethod, MediaType contentType, List<MediaType> oldaccepts)
   {
      List<WeightedMediaType> accepts = new ArrayList<WeightedMediaType>();
      for (MediaType accept : oldaccepts) accepts.add(WeightedMediaType.parse(accept));

      List<ResourceMethod> list = new ArrayList<ResourceMethod>();
      IdentityHashMap<WeightedMediaType, ResourceMethod> consumesMap = new IdentityHashMap<WeightedMediaType, ResourceMethod>();

      boolean methodMatch = false;
      boolean consumeMatch = false;

      // make a list of all compatible ResourceMethods
      // Populate the consumes identity map with media types from each ResourceMethod
      for (ResourceMethod invoker : methods)
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
            HashSet<String> allowed = new HashSet<String>();
            for (ResourceMethod invoker : methods) allowed.addAll(invoker.getHttpMethods());
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
               throw new Failure("No resource method found for options, return OK with Allow header", res);
            }
            else
            {
               Response res = Response.status(HttpResponseCodes.SC_METHOD_NOT_ALLOWED).header(HttpHeaderNames.ALLOW, allowHeaderValue).build();
               throw new Failure("No resource method found for " + httpMethod + ", return 405 with Allow header", res);
            }
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
