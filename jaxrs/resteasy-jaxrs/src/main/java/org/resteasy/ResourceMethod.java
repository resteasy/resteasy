package org.resteasy;

import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.InjectorFactory;
import org.resteasy.spi.MethodInjector;
import org.resteasy.spi.ResourceFactory;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.util.WeightedMediaType;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceMethod
{

   protected MediaType[] produces;
   protected MediaType[] consumes;
   protected List<WeightedMediaType> preferredProduces = new ArrayList<WeightedMediaType>();
   protected List<WeightedMediaType> preferredConsumes = new ArrayList<WeightedMediaType>();
   protected Set<String> httpMethods;
   protected MethodInjector methodInjector;
   protected InjectorFactory injector;
   protected ResourceFactory resource;
   protected ResteasyProviderFactory providerFactory;
   protected PathParamIndex index;
   protected Method method;

   public ResourceMethod(Class<?> clazz, Method method, InjectorFactory injector, ResourceFactory resource, ResteasyProviderFactory providerFactory, Set<String> httpMethods, PathParamIndex index)
   {
      this.injector = injector;
      this.resource = resource;
      this.providerFactory = providerFactory;
      this.httpMethods = httpMethods;
      this.index = index;
      this.method = method;
      this.methodInjector = injector.createMethodInjector(method);

      ProduceMime p = method.getAnnotation(ProduceMime.class);
      if (p == null) p = clazz.getAnnotation(ProduceMime.class);
      ConsumeMime c = method.getAnnotation(ConsumeMime.class);
      if (c == null) c = clazz.getAnnotation(ConsumeMime.class);

      if (p != null)
      {
         produces = new MediaType[p.value().length];
         int i = 0;
         for (String mediaType : p.value())
         {
            produces[i++] = MediaType.parse(mediaType);
            preferredProduces.add(WeightedMediaType.parse(mediaType));
         }
      }
      if (c != null)
      {
         consumes = new MediaType[c.value().length];
         int i = 0;
         for (String mediaType : c.value())
         {
            consumes[i++] = MediaType.parse(mediaType);
            preferredConsumes.add(WeightedMediaType.parse(mediaType));
         }
      }
      Collections.sort(preferredProduces);
      Collections.sort(preferredConsumes);
   }

   /**
    * Presorted list of preferred types, 1st entry is most preferred
    *
    * @return
    */
   public List<WeightedMediaType> getPreferredProduces()
   {
      return preferredProduces;
   }

   /**
    * Presorted list of preferred types, 1st entry is most preferred
    *
    * @return
    */
   public List<WeightedMediaType> getPreferredConsumes()
   {
      return preferredConsumes;
   }

   public Method getMethod()
   {
      return method;
   }

   public Response invoke(HttpRequest request, HttpResponse response)
   {
      // we have to check if its a ResourceLocator because we don't want the template params
      // to be populated with wrong information.
      if (!(resource instanceof ResourceLocator)) index.populateUriInfoTemplateParams(request);
      Object target = resource.createResource(request, response, injector);
      if (resource instanceof ResourceLocator) index.populateUriInfoTemplateParams(request);
      return methodInjector.invoke(request, response, target);
   }


   public boolean doesProduce(List<? extends MediaType> accepts)
   {
      if (accepts == null || accepts.size() == 0)
      {
         //System.out.println("**** no accepts " +" method: " + method);
         return true;
      }
      if (produces == null || produces.length == 0)
      {
         //System.out.println("**** no produces " +" method: " + method);
         return true;
      }

      for (MediaType accept : accepts)
      {
         for (MediaType type : preferredProduces)
         {
            if (type.isCompatible(accept))
            {
               return true;
            }
         }
      }
      return false;
   }

   public boolean doesConsume(MediaType contentType)
   {
      boolean matches = false;
      if (contentType == null)
      {
         matches = true;
      }
      else
      {
         if (consumes == null || consumes.length == 0)
         {
            matches = true;
         }
         else
         {
            for (MediaType type : preferredConsumes)
            {
               if (type.isCompatible(contentType))
               {
                  matches = true;
                  break;
               }
            }
         }
      }
      return matches;
   }

   public MediaType matchByType(List<MediaType> accepts)
   {
      if (accepts == null || accepts.size() == 0)
      {
         if (produces == null) return MediaType.parse("*/*");
         else return produces[0];
      }

      if (produces == null || produces.length == 0) return accepts.get(0);

      for (MediaType accept : accepts)
      {
         for (MediaType type : produces)
         {
            if (type.isCompatible(accept)) return type;
         }
      }
      return null;
   }

   public Set<String> getHttpMethods()
   {
      return httpMethods;
   }

   public MediaType[] getProduces()
   {
      return produces;
   }

   public MediaType[] getConsumes()
   {
      return consumes;
   }
}
