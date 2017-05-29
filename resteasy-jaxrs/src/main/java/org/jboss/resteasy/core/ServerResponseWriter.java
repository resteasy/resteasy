package org.jboss.resteasy.core;

import org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext;
import org.jboss.resteasy.core.interception.ContainerResponseContextImpl;
import org.jboss.resteasy.core.interception.ResponseContainerRequestContext;
import org.jboss.resteasy.core.interception.ServerWriterInterceptorContext;
import org.jboss.resteasy.core.registry.SegmentNode;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.resteasy_jaxrs.i18n.*;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.CommitHeaderOutputStream;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.MediaTypeHelper;

import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerResponseFilter;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServerResponseWriter
{
   public static void writeNomapResponse(BuiltResponse jaxrsResponse, final HttpRequest request, final HttpResponse response, final ResteasyProviderFactory providerFactory) throws IOException
   {
      ResourceMethodInvoker method =(ResourceMethodInvoker) request.getAttribute(ResourceMethodInvoker.class.getName());

      if (jaxrsResponse.getEntity() != null)
      {
        if (jaxrsResponse.getMediaType() == null)
        {
           setDefaultContentType(request, jaxrsResponse, providerFactory, method);
        }
        boolean addCharset = true;
        ResteasyDeployment deployment = ResteasyProviderFactory.getContextData(ResteasyDeployment.class);
        if (deployment != null)
        {
           addCharset = deployment.isAddCharset();
        }
        if (addCharset)
        {
           MediaType mt = null;
           Object o = jaxrsResponse.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
           if (o instanceof MediaType)
           {
              mt = (MediaType) o;
           }
           else
           {
              mt = MediaType.valueOf(o.toString());
           }
           if (!mt.getParameters().containsKey(MediaType.CHARSET_PARAMETER))
           {
              if (MediaTypeHelper.isTextLike(mt))
              {
                 jaxrsResponse.getHeaders().putSingle(HttpHeaders.CONTENT_TYPE, mt.withCharset(StandardCharsets.UTF_8.toString()).toString());
              }
           }
        }
      }

      executeFilters(jaxrsResponse, request, response, providerFactory, method);

      //[RESTEASY-1627] check on response.getOutputStream() to avoid resteasy-netty4 trying building a chunked response body for HEAD requests 
      if (jaxrsResponse.getEntity() == null || response.getOutputStream() == null)
      {
         response.setStatus(jaxrsResponse.getStatus());
         commitHeaders(jaxrsResponse, response);
         return;
      }

      Class type = jaxrsResponse.getEntityClass();
      Object ent = jaxrsResponse.getEntity();
      Type generic = jaxrsResponse.getGenericType();
      Annotation[] annotations = jaxrsResponse.getAnnotations();
      @SuppressWarnings(value = "unchecked")
      final MediaType mt = jaxrsResponse.getMediaType();
      MessageBodyWriter writer = providerFactory.getMessageBodyWriter(
              type, generic, annotations, mt);
      if (writer!=null)
          LogMessages.LOGGER.debugf("MessageBodyWriter: %s", writer.getClass().getName());

      if (writer == null)
      {
         throw new NoMessageBodyWriterFoundFailure(type, mt);
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
              providerFactory, ent, type, generic, annotations, mt,
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
      // Note. If we get here before the request is executed, e.g., if a ContainerRequestFilter aborts,
      // chosen and method can be null.

      MediaType chosen = (MediaType)request.getAttribute(SegmentNode.RESTEASY_CHOSEN_ACCEPT);
      boolean hasProduces = chosen != null && Boolean.valueOf(chosen.getParameters().get(SegmentNode.RESTEASY_SERVER_HAS_PRODUCES));
      hasProduces |= method != null && method.getProduces() != null && method.getProduces().length > 0;
      hasProduces |= method != null && method.getMethod().getClass().getAnnotation(Produces.class) != null;
      
      if (hasProduces)
      {
         if (chosen == null || chosen.isWildcardSubtype())
         {
            if (method != null)
            {
               // pick most specific
               if (method.getProduces().length > 0)
               {
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
               else 
               {
                  method.getMethod().getClass().getAnnotation(Produces.class);
                  for (String produceString : method.getMethod().getClass().getAnnotation(Produces.class).value())
                  {
                     MediaType produce = MediaType.valueOf(produceString);
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
         }
      }
      else
      {
         if (chosen == null)
         {
            chosen = MediaType.WILDCARD_TYPE;   
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
         List<MessageBodyWriter<?>> mbrs = providerFactory.getPossibleMessageBodyWriters(type, generic, annotations);
         List<MediaType> accepts = request.getHttpHeaders().getAcceptableMediaTypes();
         List<SortableMediaType> M = new ArrayList<SortableMediaType>();
         for (MediaType accept : accepts)
         {
            for (MessageBodyWriter<?> mbr : mbrs)
            {
               Produces produces = mbr.getClass().getAnnotation(Produces.class);
               for (String producesString : produces.value())
               {
                  MediaType produce = MediaType.valueOf(producesString);
                  if (produce.isCompatible(accept))
                  {
                     M.add(mostSpecific(produce, accept));
                  }
               }
            }
         }
     
         if (M.size() > 0)
         {
            Collections.sort(M);
            chosen = M.get(M.size() - 1);
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
         throw new NotAcceptableException(Messages.MESSAGES.illegalResponseMediaType(chosen.toString()));
      }
      if (chosen.getParameters().containsKey(SegmentNode.RESTEASY_SERVER_HAS_PRODUCES))
      {
         Map<String, String> map = new HashMap<String, String>(chosen.getParameters());
         map.remove(SegmentNode.RESTEASY_SERVER_HAS_PRODUCES);
         map.remove(SegmentNode.RESTEASY_SERVER_HAS_PRODUCES.toLowerCase());
         chosen = new MediaType(chosen.getType(), chosen.getSubtype(), map);
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
   
   private static class SortableMediaType extends MediaType implements Comparable<SortableMediaType>
   {
      double q = -1;
      double qs = -1;
      
      public SortableMediaType(MediaType m)
      {
         this(m.getType(), m.getSubtype(), m.getParameters());
      }
      
      public SortableMediaType(String type, String subtype, Map<String, String> parameters)
      {
         super(type, subtype, parameters);
         String qString = parameters.get("q");
         if (qString != null)
         {
            try
            {
               q = Double.valueOf(qString);
            }
            catch (NumberFormatException e)
            {
               // skip
            }
         }
         if (q < 0)
         {
            String qsString = parameters.get("qs");
            if (qsString != null)
            {
               try
               {
                  qs = Double.valueOf(qsString);
               }
               catch (NumberFormatException e)
               {
                  // skip
               }
            }
         }
      }
      
      @Override
      public int compareTo(SortableMediaType o)
      {
         if (this.isCompatible(o))
         {
            if (this.equals(o))
            {
               return 0;
            }
            MediaType mostSpecific = mostSpecific(this, o);
            return this.equals(mostSpecific(this, o)) ? 1 : -1;
         }
         if (this.q < o.q)
         {
            return -1;
         }
         if (this.q > o.q)
         {
            return 1;
         }
         // this.q == o.q
         if (this.qs < o.qs)
         {
            return -1;
         }
         if (this.qs > o.qs)
         {
            return 1;
         }
         return 0;
      }
   }
   
   /**
    * m1, m2 are compatible
    */
   private static SortableMediaType mostSpecific(MediaType m1, MediaType m2)
   {
      MediaType m = null;
      if (m1.getType().equals("*"))
      {
         if (m2.getType().equals("*"))
         {
            if (m1.getSubtype().equals("*"))
            {
               return new SortableMediaType(m2); // */* <= */?
            }
            else
            {
               return new SortableMediaType(m1); // */st > */?
            }
         }
         else
         {
            return new SortableMediaType(m2); // */? < t/?
         }
      }
      else
      {
         if (m2.getType().equals("*"))
         {
            return new SortableMediaType(m1); // t/? > */?
         }
         else
         {
            if (m1.getSubtype().equals("*"))
            {
               return new SortableMediaType(m2); // t/* <= t/?
            }
            else
            {
               return new SortableMediaType(m1); // t/st >= t/?
            }
         }
      }
   }
}
