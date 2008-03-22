package org.resteasy;

import org.resteasy.specimpl.ResponseImpl;
import org.resteasy.spi.Dispatcher;
import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.Registry;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.util.HttpHeaderNames;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class DefaultDispatcher extends HttpServlet implements Dispatcher
{
   protected ResteasyProviderFactory providerFactory;
   protected Registry registry;

   public ResteasyProviderFactory getProviderFactory()
   {
      return providerFactory;
   }

   public Registry getRegistry()
   {
      return registry;
   }

   public void setProviderFactory(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
   }

   public void setRegistry(Registry registry)
   {
      this.registry = registry;
   }

   public void invoke(HttpRequest in, HttpResponse response)
   {
      ResourceMethod invoker = null;
      try
      {
         invoker = registry.getResourceInvoker(in.getHttpMethod(), in.getUri().getPathSegments(), in.getHttpHeaders().getMediaType(), in.getHttpHeaders().getAcceptableMediaTypes());
      }
      catch (Failure e)
      {
         try
         {
            response.sendError(e.getErrorCode());
         }
         catch (IOException e1)
         {
            throw new RuntimeException(e1);
         }
         e.printStackTrace();
         return;
      }
      if (invoker == null)
      {
         try
         {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
         return;
      }
      if (!invoker.getHttpMethods().contains(in.getHttpMethod()))
      {
         try
         {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
         return;
      }


      try
      {
         ResponseImpl jaxrsResponse = null;
         try
         {
            jaxrsResponse = invoker.invoke(in);
         }
         catch (Failure e)
         {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            e.printStackTrace();
            return;
         }
         if (jaxrsResponse.getMetadata() != null && jaxrsResponse.getMetadata().size() > 0)
         {
            response.getOutputHeaders().putAll(jaxrsResponse.getMetadata());
         }
         for (NewCookie cookie : jaxrsResponse.getNewCookies())
         {
            response.addNewCookie(cookie);
         }

         if (jaxrsResponse.getEntity() != null)
         {
            MediaType responseContentType = resolveContentType(invoker, in, jaxrsResponse);
            writeResponse(response, invoker, jaxrsResponse.getEntity(), responseContentType);
         }
         response.setStatus(jaxrsResponse.getStatus());

      }
      catch (Exception e)
      {
         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         e.printStackTrace();
         return;
      }
   }

   protected void writeResponse(HttpResponse response, ResourceMethod invoker, Object entity, MediaType responseContentType)
   {

      Class type = entity.getClass();

      Type genericType = null;
      if (!Response.class.equals(invoker.getMethod().getReturnType()))
      {
         genericType = invoker.getMethod().getGenericReturnType();
      }

      Annotation[] annotations = invoker.getMethod().getAnnotations();

      MessageBodyWriter writer = providerFactory.createMessageBodyWriter(type, genericType, annotations, responseContentType);
      if (writer == null)
      {
         throw new RuntimeException("Could not find MessageBodyWriter for response object of type: " + entity.getClass() + " of media type: " + responseContentType);
      }
      try
      {
         long size = writer.getSize(entity);
         //System.out.println("Writer: " + writer.getClass().getName());
         //System.out.println("JAX-RS Content Size: " + size);
         response.getOutputHeaders().putSingle(HttpHeaderNames.CONTENT_LENGTH, Integer.toString((int) size));
         response.getOutputHeaders().putSingle(HttpHeaderNames.CONTENT_TYPE, responseContentType.toString());
         writer.writeTo(entity, genericType, invoker.getMethod().getAnnotations(), responseContentType, response.getOutputHeaders(), response.getOutputStream());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   protected MediaType resolveContentType(ResourceMethod invoker, HttpRequest in, ResponseImpl responseImpl)
   {
      Object contentType = responseImpl.getMetadata().getFirst(HttpHeaderNames.CONTENT_TYPE);
      MediaType responseContentType = null;
      if (contentType != null) // if set by the response
      {
         //System.out.println("content type was set: " + contentType);
         responseContentType = MediaType.parse(contentType.toString());
      }
      else
      {
         //System.out.println("finding content type from @ProduceMime");
         responseContentType = invoker.matchByType(in.getHttpHeaders().getAcceptableMediaTypes());
      }
      if (responseContentType == null)
      {
         responseContentType = MediaType.parse("*/*");
      }
      return responseContentType;
   }
}
