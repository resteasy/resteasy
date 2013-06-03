package org.jboss.resteasy.core;

import org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext;
import org.jboss.resteasy.core.interception.ContainerResponseContextImpl;
import org.jboss.resteasy.core.interception.ResponseContainerRequestContext;
import org.jboss.resteasy.core.interception.ServerWriterInterceptorContext;
import org.jboss.resteasy.core.registry.SegmentNode;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.CommitHeaderOutputStream;
import org.jboss.resteasy.util.HttpHeaderNames;

import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
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

      if (jaxrsResponse.getEntity() != null && jaxrsResponse.getMediaType() == null)
      {
         setDefaultContentType(request, jaxrsResponse, providerFactory, method);
      }

      executeFilters(jaxrsResponse, request, response, providerFactory, method);

      if (jaxrsResponse.getEntity() == null || request.getHttpMethod().equalsIgnoreCase("HEAD"))
      {
         response.setStatus(jaxrsResponse.getStatus());
         commitHeaders(jaxrsResponse, response);
         return;
      }

      Class type = jaxrsResponse.getEntityClass();
      Object ent = jaxrsResponse.getEntity();
      Type generic = jaxrsResponse.getGenericType();
      if (generic == null)
      {
         if (method != null && !Response.class.isAssignableFrom(method.getMethod().getReturnType())) generic = method.getGenericReturnType();
         else generic = type;
      }
      Annotation[] annotations = jaxrsResponse.getAnnotations();
      if (annotations == null && method != null)
      {
         annotations = method.getMethodAnnotations();
      }
      MessageBodyWriter writer = providerFactory.getMessageBodyWriter(
              type, generic, annotations, jaxrsResponse.getMediaType());

      if (writer == null)
      {
         throw new NoMessageBodyWriterFoundFailure(type, jaxrsResponse.getMediaType());
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

      WriterInterceptor[] writerInterceptors = null;
      if (method != null)
      {
         writerInterceptors = method.getWriterInterceptors();
      }
      else
      {
         writerInterceptors = providerFactory.getServerWriterInterceptorRegistry().postMatch(null, null);
      }

      AbstractWriterInterceptorContext writerContext =  new ServerWriterInterceptorContext(writerInterceptors,
              providerFactory, ent, type, generic, annotations, jaxrsResponse.getMediaType(),
              jaxrsResponse.getMetadata(), os, request);
      writerContext.proceed();
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

   protected static void setDefaultContentType(HttpRequest request, BuiltResponse jaxrsResponse, ResteasyProviderFactory providerFactory, ResourceMethodInvoker method)
   {
      MediaType chosen = (MediaType)request.getAttribute(SegmentNode.RESTEASY_CHOSEN_ACCEPT);
      if (chosen != null && chosen.isWildcardSubtype()) chosen = null;
      if (chosen == null)
      {
         if (method != null)
         {
            // pick most specific
            for (MediaType produce : method.getProduces())
            {

               if (!produce.isWildcardType())
               {
                  chosen = produce;
                  if (!produce.isWildcardSubtype())
                  {
                     break;
                  }
               }
            }
         }
      }
      if (chosen == null)
      {
         chosen = MediaType.WILDCARD_TYPE;
         Class type = jaxrsResponse.getEntityClass();
         Object ent = jaxrsResponse.getEntity();
         Type generic = jaxrsResponse.getGenericType();
         if (generic == null)
         {
            if (method != null && !Response.class.isAssignableFrom(method.getMethod().getReturnType())) generic = method.getGenericReturnType();
            else generic = type;
         }
         Annotation[] annotations = jaxrsResponse.getAnnotations();
         if (annotations == null && method != null)
         {
            annotations = method.getMethodAnnotations();
         }
         MediaType mt = providerFactory.getConcreteMediaTypeFromMessageBodyWriters(type, generic, annotations, chosen);
         if (mt != null)
         {
            jaxrsResponse.getHeaders().putSingle(HttpHeaders.CONTENT_TYPE, mt);
            return;
         }
      }

      if (chosen.isWildcardType())
      {
         chosen = MediaType.APPLICATION_OCTET_STREAM_TYPE;
      }
      else if (chosen.isWildcardSubtype() && chosen.getSubtype().equals("application"))
      {
         chosen = MediaType.APPLICATION_OCTET_STREAM_TYPE;
      }
      else if (chosen.isWildcardSubtype())
      {
         throw new NotAcceptableException("Illegal response media type: " + chosen.toString());
      }
      jaxrsResponse.getHeaders().putSingle(HttpHeaders.CONTENT_TYPE, chosen);
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
