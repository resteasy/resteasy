package org.resteasy;

import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.InjectorFactory;
import org.resteasy.spi.MethodInjector;
import org.resteasy.spi.ResourceFactory;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.util.HttpHeaderNames;
import org.resteasy.util.HttpResponseCodes;
import org.resteasy.util.WeightedMediaType;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceMethod implements ResourceInvoker
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
   protected String[] rolesAllowed;
   protected boolean denyAll;

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
            produces[i++] = MediaType.valueOf(mediaType);
            preferredProduces.add(WeightedMediaType.valueOf(mediaType));
         }
      }
      if (c != null)
      {
         consumes = new MediaType[c.value().length];
         int i = 0;
         for (String mediaType : c.value())
         {
            consumes[i++] = MediaType.valueOf(mediaType);
            preferredConsumes.add(WeightedMediaType.valueOf(mediaType));
         }
      }
      Collections.sort(preferredProduces);
      Collections.sort(preferredConsumes);

      RolesAllowed allowed = clazz.getAnnotation(RolesAllowed.class);
      RolesAllowed methodAllowed = method.getAnnotation(RolesAllowed.class);
      if (methodAllowed != null) allowed = methodAllowed;
      if (allowed != null)
      {
         rolesAllowed = allowed.value();
      }

      denyAll = (clazz.isAnnotationPresent(DenyAll.class) && method.isAnnotationPresent(RolesAllowed.class) == false && method.isAnnotationPresent(PermitAll.class) == false) || method.isAnnotationPresent(DenyAll.class);


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

   public void invoke(HttpRequest request, HttpResponse response) throws IOException
   {
      Object target = resource.createResource(request, response, injector);
      invoke(request, response, target);
   }

   public void checkAuthorized()
   {
      if (denyAll) throw new Failure(HttpResponseCodes.SC_UNAUTHORIZED);
      if (rolesAllowed == null) return;

      SecurityContext context = providerFactory.getContextData(SecurityContext.class);
      if (context != null)
      {
         for (String role : rolesAllowed)
         {
            if (context.isUserInRole(role)) return;
         }
         throw new Failure(HttpResponseCodes.SC_UNAUTHORIZED);
      }
   }

   public void invoke(HttpRequest request, HttpResponse response, Object target) throws IOException
   {

      checkAuthorized();

      Response jaxrsResponse = null;
      try
      {
         index.populateUriInfoTemplateParams(request);
         jaxrsResponse = methodInjector.invoke(request, response, target);
      }
      catch (Failure e)
      {
         response.sendError(HttpServletResponse.SC_BAD_REQUEST);
         e.printStackTrace();
         return;
      }
      response.setStatus(jaxrsResponse.getStatus());
      if (jaxrsResponse.getMetadata() != null)
      {
         List cookies = jaxrsResponse.getMetadata().get(HttpHeaderNames.SET_COOKIE);
         if (cookies != null)
         {
            Iterator it = cookies.iterator();
            while (it.hasNext())
            {
               Object next = it.next();
               if (next instanceof NewCookie)
               {
                  NewCookie cookie = (NewCookie) next;
                  response.addNewCookie(cookie);
                  it.remove();
               }
            }
            if (cookies.size() < 1) jaxrsResponse.getMetadata().remove(HttpHeaderNames.SET_COOKIE);
         }
      }
      if (jaxrsResponse.getMetadata() != null && jaxrsResponse.getMetadata().size() > 0)
      {
         response.getOutputHeaders().putAll(jaxrsResponse.getMetadata());
      }

      if (jaxrsResponse.getEntity() != null)
      {
         MediaType responseContentType = resolveContentType(request, jaxrsResponse);
         writeResponse(response, jaxrsResponse.getEntity(), responseContentType);
      }
   }

   protected void writeResponse(HttpResponse response, Object entity, MediaType responseContentType)
   {

      Class type = entity.getClass();

      Type genericType = null;
      if (!Response.class.equals(getMethod().getReturnType()))
      {
         genericType = getMethod().getGenericReturnType();
      }

      Annotation[] annotations = getMethod().getAnnotations();

      MessageBodyWriter writer = providerFactory.createMessageBodyWriter(type, genericType, annotations, responseContentType);
      if (writer == null)
      {
         throw new RuntimeException("Could not find MessageBodyWriter for response object of type: " + entity.getClass() + " of media type: " + responseContentType);
      }
      //System.out.println("MessageBodyWriter class is: " + writer.getClass().getName());
      //System.out.println("Response content type: " + responseContentType);
      try
      {
         long size = writer.getSize(entity);
         //System.out.println("Writer: " + writer.getClass().getName());
         //System.out.println("JAX-RS Content Size: " + size);
         response.getOutputHeaders().putSingle(HttpHeaderNames.CONTENT_LENGTH, Integer.toString((int) size));
         response.getOutputHeaders().putSingle(HttpHeaderNames.CONTENT_TYPE, responseContentType.toString());
         writer.writeTo(entity, type, genericType, getMethod().getAnnotations(), responseContentType, response.getOutputHeaders(), response.getOutputStream());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   protected MediaType resolveContentType(HttpRequest in, Response responseImpl)
   {
      Object contentType = responseImpl.getMetadata().getFirst(HttpHeaderNames.CONTENT_TYPE);
      MediaType responseContentType = null;
      if (contentType != null) // if set by the response
      {
         //System.out.println("content type was set: " + contentType);
         responseContentType = MediaType.valueOf(contentType.toString());
      }
      else
      {
         //System.out.println("finding content type from @ProduceMime");
         responseContentType = matchByType(in.getHttpHeaders().getAcceptableMediaTypes());
      }
      if (responseContentType == null)
      {
         responseContentType = MediaType.valueOf("*/*");
      }
      return responseContentType;
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
         if (produces == null) return MediaType.valueOf("*/*");
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
