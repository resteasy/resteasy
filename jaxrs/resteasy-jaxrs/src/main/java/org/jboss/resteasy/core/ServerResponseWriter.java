package org.jboss.resteasy.core;

import org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext;
import org.jboss.resteasy.core.interception.ContainerResponseContextImpl;
import org.jboss.resteasy.core.interception.ResponseContainerRequestContext;
import org.jboss.resteasy.core.interception.ServerWriterInterceptorContext;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.CommitHeaderOutputStream;
import org.jboss.resteasy.util.HttpHeaderNames;

import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.WriterInterceptor;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServerResponseWriter
{
   private final static Logger logger = Logger.getLogger(ServerResponseWriter.class);

   public static void writeNomapResponse(BuiltResponse jaxrsResponse, final HttpRequest request, final HttpResponse response, final ResteasyProviderFactory providerFactory) throws IOException
   {
      ResourceMethodInvoker method = (ResourceMethodInvoker) request.getAttribute(ResourceMethodInvoker.class.getName());

      executeFilters(jaxrsResponse, request, response, providerFactory, method);

      if (jaxrsResponse.getEntity() == null)
      {
         response.setStatus(jaxrsResponse.getStatus());
         commitHeaders(jaxrsResponse, response);
         return;
      }

      Type generic = jaxrsResponse.getGenericType();
      if (generic == null && method != null)
      {
         generic = method.getGenericReturnType();
      }
      Class type = jaxrsResponse.getEntityClass();
      Object ent = jaxrsResponse.getEntity();
      Annotation[] annotations = jaxrsResponse.getAnnotations();
      if (annotations == null && method != null)
      {
         annotations = method.getMethodAnnotations();
      }
      MediaType contentType = resolveContentType(jaxrsResponse);
      MessageBodyWriter writer = providerFactory.getMessageBodyWriter(
              type, generic, annotations, contentType);

      if (writer == null)
      {
         throw new NoMessageBodyWriterFoundFailure(type, contentType);
      }

      response.setStatus(jaxrsResponse.getStatus());
      final BuiltResponse built = jaxrsResponse;
      CommitHeaderOutputStream.CommitCallback callback = new CommitHeaderOutputStream.CommitCallback()
      {
         private boolean committed;

         @Override
         public void commit()
         {
            if (committed) return;
            committed = true;
            commitHeaders(built, response);
         }
      };
      OutputStream os = new CommitHeaderOutputStream(response.getOutputStream(), callback);

      long size = writer.getSize(ent, type, generic, annotations, contentType);
      if (size > -1) jaxrsResponse.getMetadata().putSingle(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(size));

      WriterInterceptor[] writerInterceptors = null;
      if (method != null)
      {
         writerInterceptors = method.getWriterInterceptors();
      }
      else
      {
         writerInterceptors = providerFactory.getServerWriterInterceptorRegistry().postMatch(null, null);
      }

      if (writerInterceptors == null || writerInterceptors.length == 0)
      {
         writer.writeTo(ent, type, generic, annotations,
                 contentType, jaxrsResponse.getMetadata(), os);
      }
      else
      {
         AbstractWriterInterceptorContext writerContext =  new ServerWriterInterceptorContext(writerInterceptors, writer, ent, type, generic, annotations, contentType, jaxrsResponse.getMetadata(), os, request);
         writerContext.proceed();
      }
      callback.commit(); // just in case the output stream is never used
   }

   private static void executeFilters(BuiltResponse jaxrsResponse, HttpRequest request, HttpResponse response, ResteasyProviderFactory providerFactory, ResourceMethodInvoker method) throws IOException
   {
      ContainerResponseFilter[] responseFilters = null;

      if (method != null)
      {
         responseFilters = method.getResponseFilters();
      }
      else
      {
         responseFilters = providerFactory.getContainerResponseFilterRegistry().postMatch(null, null);
      }

      if (responseFilters != null)
      {
         ResponseContainerRequestContext requestContext = new ResponseContainerRequestContext(request);
         ContainerResponseContextImpl responseContext = new ContainerResponseContextImpl(request, response, jaxrsResponse);
         for (ContainerResponseFilter filter : responseFilters)
         {
            filter.filter(requestContext, responseContext);
         }
      }
   }

   public static MediaType resolveContentType(BuiltResponse response)
   {
      MediaType responseContentType = null;
      Object type = response.getMetadata().getFirst(HttpHeaderNames.CONTENT_TYPE);
      if (type == null)
      {
         return MediaType.WILDCARD_TYPE;
      }
      if (type instanceof MediaType)
      {
         responseContentType = (MediaType) type;
      }
      else
      {
         responseContentType = MediaType.valueOf(type.toString());
      }
      return responseContentType;
   }

   public static void commitHeaders(BuiltResponse jaxrsResponse, HttpResponse response)
   {
      if (jaxrsResponse.getMetadata() != null)
      {
         List<Object> cookies = jaxrsResponse.getMetadata().get(
                 HttpHeaderNames.SET_COOKIE);
         if (cookies != null)
         {
            Iterator<Object> it = cookies.iterator();
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
            if (cookies.size() < 1)
               jaxrsResponse.getMetadata().remove(HttpHeaderNames.SET_COOKIE);
         }
      }
      if (jaxrsResponse.getMetadata() != null
              && jaxrsResponse.getMetadata().size() > 0)
      {
         response.getOutputHeaders().putAll(jaxrsResponse.getMetadata());
      }
   }
}
