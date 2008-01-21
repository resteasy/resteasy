package org.resteasy;

import org.resteasy.spi.HttpInput;
import org.resteasy.spi.HttpOutput;
import org.resteasy.spi.ResourceFactory;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.util.HttpHeaderNames;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
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


   public void invoke(HttpInput input, HttpOutput output)
   {
      Object resource = factory.createResource(input, output);
      populateUriParams(input);
      Object[] args = getArguments(input);
      try
      {
         Object rtn = method.invoke(resource, args);
         if (method.getReturnType().equals(void.class)) return;
         MediaType rtnType = matchByType(input.getHttpHeaders().getAcceptableMediaTypes());
         MessageBodyWriter writer = providerFactory.createMessageBodyWriter(method.getReturnType(), rtnType);
         try
         {
            long size = writer.getSize(rtn);
            output.getOutputHeaders().putSingle(HttpHeaderNames.CONTENT_LENGTH, ((Long) size).toString());
            if (rtnType != null)
            {
               output.getOutputHeaders().putSingle(HttpHeaderNames.CONTENT_TYPE, rtnType.toString());
            }
            writer.writeTo(rtn, rtnType, output.getOutputHeaders(), output.getOutputStream());
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(e);
      }
      catch (InvocationTargetException e)
      {
         throw new RuntimeException(e.getCause());
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
      if (accepts == null || accepts.size() == 0) return true;
      if (produces == null || produces.length == 0) return true;

      for (MediaType accept : accepts)
      {
         for (MediaType type : produces)
         {
            if (type.isCompatible(accept))
            {
               matches = true;
               break;
            }
         }
      }
      return matches;
   }

   public MediaType matchByType(List<MediaType> accepts)
   {
      if (accepts == null || accepts.size() == 0) return produces[0];

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
