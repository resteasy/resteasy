package org.resteasy;

import org.resteasy.specimpl.ResponseImpl;
import org.resteasy.spi.HttpInput;
import org.resteasy.spi.ResourceFactory;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.util.HttpResponseCodes;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceMethod extends ResourceInvoker
{

   protected MediaType[] produces;
   protected MediaType[] consumes;
   protected Set<String> httpMethods;

   public ResourceMethod(String path, Class<?> clazz, Method method, ResourceFactory factory, ResteasyProviderFactory providerFactory, Set<String> httpMethods)
   {
      super(path, factory, method, providerFactory);
      this.httpMethods = httpMethods;

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
         }
      }
      if (c != null)
      {
         consumes = new MediaType[c.value().length];
         int i = 0;
         for (String mediaType : c.value())
         {
            consumes[i++] = MediaType.parse(mediaType);
         }
      }


   }


   public ResponseImpl invoke(HttpInput input)
   {
      Object resource = null;
      try
      {
         resource = factory.createResource(input);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      Object[] args = null;
      try
      {
         populateUriParams(input);
         args = new Object[0];
         args = getArguments(input);
      }
      catch (Exception e)
      {
         throw new Failure("Failed processing arguments of " + method.toString(), e, HttpResponseCodes.SC_BAD_REQUEST);
      }
      try
      {
         Object rtn = method.invoke(resource, args);
         if (method.getReturnType().equals(void.class)) return new ResponseImpl();
         if (method.getReturnType().equals(ResponseImpl.class))
         {
            return (ResponseImpl) rtn;
         }
         else if (method.getReturnType().equals(Response.class))
            throw new RuntimeException("You must use JAX-RS apis to create Response objects");

         ResponseImpl response = new ResponseImpl();
         response.setEntity(rtn);
         response.setStatus(HttpResponseCodes.SC_OK);
         return response;
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException("Failed processing of " + method.toString(), e);
      }
      catch (InvocationTargetException e)
      {
         throw new RuntimeException("Failed processing " + method.toString(), e.getCause());
      }
      catch (IllegalArgumentException e)
      {
         String msg = "Bad arguments passed to " + method.toString() + "  (";
         boolean first = false;
         for (Object arg : args)
         {
            if (!first)
            {
               first = true;
            }
            else
            {
               msg += ",";
            }
            if (arg == null)
            {
               msg += " null";
               continue;
            }
            msg += " " + arg;
         }
         throw new RuntimeException(msg, e);
      }
   }

   public boolean matchByType(MediaType contentType, List<MediaType> accepts)
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
            for (MediaType type : consumes)
            {
               if (type.isCompatible(contentType))
               {
                  matches = true;
                  break;
               }
            }
         }
      }
      if (!matches) return false;
      matches = false;
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
         for (MediaType type : produces)
         {
            if (type.isCompatible(accept))
            {
               //System.out.println("**** produces: " + type + " matches accept: " + accept + " method: " + method);
               matches = true;
               break;
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
}
