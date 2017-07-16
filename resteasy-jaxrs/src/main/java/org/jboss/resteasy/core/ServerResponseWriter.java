package org.jboss.resteasy.core;

import org.jboss.resteasy.core.interception.jaxrs.AbstractWriterInterceptorContext;
import org.jboss.resteasy.core.interception.jaxrs.ContainerResponseContextImpl;
import org.jboss.resteasy.core.interception.jaxrs.ResponseContainerRequestContext;
import org.jboss.resteasy.core.interception.jaxrs.ServerWriterInterceptorContext;
import org.jboss.resteasy.core.registry.SegmentNode;
import org.jboss.resteasy.resteasy_jaxrs.i18n.*;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
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
import java.util.Map.Entry;

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
      response.setOutputStream(writerContext.getOutputStream()); //propagate interceptor changes on the outputstream to the response
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
         //we have @Produces on the resource (method or class), so we're not going to scan for @Produces on MBws  
         if (!isConcrete(chosen))
         {
            //no concrete content-type set, compute again (JAX-RS 2.0 Section 3.8 step 2, first and second bullets)
            MediaType[] produces = null;
            if (method != null)
            {
               // pick most specific
               if (method.getProduces().length > 0)
               {
                  produces = method.getProduces();
               }
               else 
               {
                  method.getMethod().getClass().getAnnotation(Produces.class);
                  String[] producesValues = method.getMethod().getClass().getAnnotation(Produces.class).value();
                  produces = new MediaType[producesValues.length];
                  for (int i = 0; i < producesValues.length; i++)
                  {
                     produces[i] = MediaType.valueOf(producesValues[i]);
                  }
               }
            }
            //JAX-RS 2.0 Section 3.8.3
            if (produces == null)
            {
               produces = new MediaType[]{MediaType.WILDCARD_TYPE};
            }
            //JAX-RS 2.0 Section 3.8.4
            List<MediaType> accepts = request.getHttpHeaders().getAcceptableMediaTypes();
            //JAX-RS 2.0 Section 3.8.5
            List<SortableMediaType> M = new ArrayList<SortableMediaType>();
            boolean hasStarStar = false;
            boolean hasApplicationStar = false;
            for (MediaType accept : accepts)
            {
               for (MediaType produce : produces)
               {
                  SortableMediaType ms = mostSpecific(produce, null, accept, null);
                  if (ms.isWildcardSubtype())
                  {
                     hasStarStar |= ms.isWildcardType();
                     hasApplicationStar |= ms.getType().equals("application");
                  }
                  M.add(ms);
               }
            }
            chosen = chooseFromM(chosen, M, hasStarStar, hasApplicationStar);
         }
      }
      else
      {
         //no @Produces on resource (class / method), use MBWs
         chosen = MediaType.WILDCARD_TYPE;
         //JAX-RS 2.0 Section 3.8.2 step 3
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
         Map<MessageBodyWriter<?>, Class<?>> mbrs = providerFactory.getPossibleMessageBodyWritersMap(type, generic, annotations);
         //JAX-RS 2.0 Section 3.8.4
         List<MediaType> accepts = request.getHttpHeaders().getAcceptableMediaTypes();
         //JAX-RS 2.0 Section 3.8.5
         List<SortableMediaType> M = new ArrayList<SortableMediaType>();
         boolean hasStarStar = false;
         boolean hasApplicationStar = false;
         if (mbrs.isEmpty())
         {
            for (MediaType accept : accepts)
            {
               MediaType produce = MediaType.WILDCARD_TYPE;
               if (produce.isCompatible(accept))
               {
                  SortableMediaType ms = mostSpecific(produce, null, accept, null);
                  if (ms.isWildcardSubtype())
                  {
                     hasStarStar |= ms.isWildcardType();
                     hasApplicationStar |= ms.getType().equals("application");
                  }
                  M.add(ms);
               }
            }
         }
         else
         {
            for (MediaType accept : accepts)
            {
               for (Entry<MessageBodyWriter<?>, Class<?>> e : mbrs.entrySet())
               {
                  MessageBodyWriter<?> mbr = e.getKey();
                  Class<?> wt = e.getValue();
                  Produces produces = mbr.getClass().getAnnotation(Produces.class);
                  for (String producesString : produces.value())
                  {
                     MediaType produce = MediaType.valueOf(producesString);
                     if (produce.isCompatible(accept))
                     {
                        SortableMediaType ms = mostSpecific(produce, wt, accept, null);
                        if (ms.isWildcardSubtype())
                        {
                           hasStarStar |= ms.isWildcardType();
                           hasApplicationStar |= ms.getType().equals("application");
                        }
                        M.add(ms);
                     }
                  }
               }
            }
         }
         chosen = chooseFromM(chosen, M, hasStarStar, hasApplicationStar);
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
   
   private static MediaType chooseFromM(MediaType currentChoice, List<SortableMediaType> M, boolean hasStarStar, boolean hasApplicationStar)
   {
      //JAX-RS 2.0 Section 3.8.6
      if (M.isEmpty())
      {
         throw new NotAcceptableException();
      }
      //JAX-RS 2.0 Section 3.8.7
      Collections.sort(M);
      //JAX-RS 2.0 Section 3.8.8
      for (SortableMediaType m : M)
      {
         if (isConcrete(m))
         {
            currentChoice = m;
            break;
         }
      }
      if (!isConcrete(currentChoice))
      {
         //JAX-RS 2.0 Section 3.8.9
         if (hasStarStar || hasApplicationStar)
         {
            currentChoice = MediaType.APPLICATION_OCTET_STREAM_TYPE;
         }
         else
         {
            //JAX-RS 2.0 Section 3.8.10
            throw new NotAcceptableException();
         }
      }
      return currentChoice;
   }
   
   private static boolean isConcrete(MediaType m)
   {
      return m != null && !m.isWildcardType() && !m.isWildcardSubtype();
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
      Class<?> writerType = null;
      
      public SortableMediaType(MediaType m)
      {
         this(m.getType(), m.getSubtype(), m.getParameters());
      }
      
      public SortableMediaType(MediaType m, Class<?> writerType)
      {
         this(m.getType(), m.getSubtype(), m.getParameters(), writerType);
      }
      
      public SortableMediaType(String type, String subtype, Map<String, String> parameters, Class<?> writerType)
      {
         this(type, subtype, parameters);
         this.writerType = writerType;
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
         if (o.isCompatible(this))
         {
            if (o.equals(this))
            {
               return 0;
            }
            MediaType mostSpecific = mostSpecific(o, this);
            return o.equals(mostSpecific(o, this)) ? 1 : -1;
         }
         if (o.q < this.q)
         {
            return -1;
         }
         if (o.q > this.q)
         {
            return 1;
         }
         // zzzz.q == this.q
         if (o.qs < this.qs)
         {
            return -1;
         }
         if (o.qs > this.qs)
         {
            return 1;
         }
         if (o.writerType == this.writerType) return 0;
         if (o.writerType == null) return -1;
         if (this.writerType == null) return 1;
         if (o.writerType.isAssignableFrom(this.writerType)) return -1;
         if (this.writerType.isAssignableFrom(o.writerType)) return 1;
         return 0;
      }
   }
   
   
   private static SortableMediaType mostSpecific(MediaType m1, MediaType m2)
   {
      Class<?> wt1 = m1 instanceof SortableMediaType ? ((SortableMediaType)m1).writerType : null;
      Class<?> wt2 = m2 instanceof SortableMediaType ? ((SortableMediaType)m2).writerType : null;
      return mostSpecific(m1, wt1, m2, wt2);
   }
   
   /**
    * m1, m2 are compatible
    */
   private static SortableMediaType mostSpecific(MediaType m1, Class<?> wt1, MediaType m2, Class<?> wt2)
   {
      if (m1.getType().equals("*"))
      {
         if (m2.getType().equals("*"))
         {
            if (m1.getSubtype().equals("*"))
            {
               return new SortableMediaType(m2, wt2); // */* <= */?
            }
            else
            {
               return new SortableMediaType(m1, wt1); // */st > */?
            }
         }
         else
         {
            return new SortableMediaType(m2, wt2); // */? < t/?
         }
      }
      else
      {
         if (m2.getType().equals("*"))
         {
            return new SortableMediaType(m1, wt1); // t/? > */?
         }
         else
         {
            if (m1.getSubtype().equals("*"))
            {
               return new SortableMediaType(m2, wt2); // t/* <= t/?
            }
            else
            {
               return new SortableMediaType(m1, wt1); // t/st >= t/?
            }
         }
      }
   }
}
