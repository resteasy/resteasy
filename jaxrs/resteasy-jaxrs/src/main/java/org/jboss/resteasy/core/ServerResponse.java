package org.jboss.resteasy.core;

import org.jboss.resteasy.core.interception.ContainerResponseContextImpl;
import org.jboss.resteasy.core.interception.ResponseContainerRequestContext;
import org.jboss.resteasy.core.interception.WriterInterceptorContextImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.WriterException;
import org.jboss.resteasy.util.CommitHeaderOutputStream;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.MessageProcessingException;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.WriterInterceptor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServerResponse extends Response implements Serializable
{
   private static final long serialVersionUID = 3458762447649978756L;
   protected Object entity;
   protected int status = HttpResponseCodes.SC_OK;
   protected Headers<Object> metadata = new Headers<Object>();
   protected Annotation[] annotations;
   protected Type genericType;
   protected ContainerResponseFilter[] responseFilters;
   protected WriterInterceptor[] writerInterceptors;
   protected Method resourceMethod;
   protected Class resourceClass;
   protected boolean headersCommitted;

   public ServerResponse(Object entity, int status, Headers<Object> metadata)
   {
      this.entity = entity;
      this.status = status;
      this.metadata = metadata;
   }

   public ServerResponse()
   {
   }

   public static ServerResponse convertToServerResponse(Response response)
   {
      if (response instanceof ServerResponse) return (ServerResponse) response;
      ServerResponse serverResponse = new ServerResponse();
      serverResponse.entity = response.getEntity();
      serverResponse.status = response.getStatus();
      if (response.getMetadata() != null)
      {
         serverResponse.metadata.putAll(response.getMetadata());
      }
      return serverResponse;
   }

   /**
    * JAX-RS method invoked on.  FYI, this method may return null, specifically within the context of an async HTTP
    * request as contextual
    * information is not available to the container.
    *
    * @return
    */
   public Method getResourceMethod()
   {
      return resourceMethod;
   }

   public void setResourceMethod(Method resourceMethod)
   {
      this.resourceMethod = resourceMethod;
   }

   /**
    * Resource class. FYI, this method may return null, specifically within the context of an async HTTP request as contextual
    * information is not available to the container
    *
    * @return
    */
   public Class getResourceClass()
   {
      return resourceClass;
   }

   public void setResourceClass(Class resourceClass)
   {
      this.resourceClass = resourceClass;
   }

   public WriterInterceptor[] getWriterInterceptors()
   {
      return writerInterceptors;
   }

   public void setWriterInterceptors(WriterInterceptor[] writerInterceptors)
   {
      this.writerInterceptors = writerInterceptors;
   }

   public ContainerResponseFilter[] getResponseFilters()
   {
      return responseFilters;
   }

   public void setResponseFilters(ContainerResponseFilter[] responseFilters)
   {
      this.responseFilters = responseFilters;
   }

   @Override
   public Object getEntity()
   {
      return entity;
   }

   @Override
   public int getStatus()
   {
      return status;
   }

   @Override
   public StatusType getStatusInfo()
   {
      return Status.fromStatusCode(status);
   }

   @Override
   public MultivaluedMap<String, Object> getMetadata()
   {
      return metadata;
   }

   public void setEntity(Object entity)
   {
      this.entity = entity;
   }

   public void setStatus(int status)
   {
      this.status = status;
   }

   public void setMetadata(MultivaluedMap<String, Object> metadata)
   {
      this.metadata.clear();
      this.metadata.putAll(metadata);
   }

   public Annotation[] getAnnotations()
   {
      return annotations;
   }

   public void setAnnotations(Annotation[] annotations)
   {
      this.annotations = annotations;
   }

   public Type getGenericType()
   {
      return genericType;
   }

   public void setGenericType(Type genericType)
   {
      this.genericType = genericType;
   }

   /**
    * If there is an entity, headers are not converted to a string and set on the HttpResponse until the output stream is written to.  If there is an exception
    * thrown then the headers are never written to the response.  We do this so that on error conditions there is a clean response.
    *
    * @param request
    * @param response
    * @param providerFactory
    * @throws WriterException
    */
   public void writeTo(HttpRequest request, HttpResponse response, ResteasyProviderFactory providerFactory) throws WriterException
   {
      if (responseFilters != null)
      {
         ResponseContainerRequestContext requestContext = new ResponseContainerRequestContext(request);
         ContainerResponseContextImpl responseContext = new ContainerResponseContextImpl(this, response, request.getProperties());
         for (ContainerResponseFilter filter : responseFilters)
         {
            try
            {
               filter.filter(requestContext, responseContext);
            }
            catch (IOException e)
            {
               throw new RuntimeException(e);
            }
         }
      }
      if (entity == null)
      {
         response.setStatus(getStatus());
         commitHeaders(response);
         return;
      }

      Type generic = genericType;
      Class type = entity.getClass();
      Object ent = entity;
      if (entity instanceof GenericEntity)
      {
         GenericEntity ge = (GenericEntity) entity;
         generic = ge.getType();
         ent = ge.getEntity();
         type = ent.getClass();
      }
      MediaType contentType = resolveContentType();
      MessageBodyWriter writer = providerFactory.getMessageBodyWriter(
              type, generic, annotations, contentType);

      if (writer == null)
      {
         throw new NoMessageBodyWriterFoundFailure(type, contentType);
      }

      try
      {
         response.setStatus(getStatus());
         final HttpResponse theResponse = response;
         CommitHeaderOutputStream.CommitCallback callback = new CommitHeaderOutputStream.CommitCallback()
         {
            private boolean committed;

            @Override
            public void commit()
            {
               if (committed) return;
               committed = true;
               commitHeaders(theResponse);
            }
         };
         OutputStream os = new CommitHeaderOutputStream(response.getOutputStream(), callback);

         long size = writer.getSize(ent, type, generic, annotations, contentType);
         if (size > -1) getMetadata().putSingle(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(size));


         if (writerInterceptors == null || writerInterceptors.length == 0)
         {
            writer.writeTo(ent, type, generic, annotations,
                    contentType, getMetadata(), os);
         }
         else
         {
            WriterInterceptorContextImpl writerContext =  new WriterInterceptorContextImpl(writerInterceptors, writer, ent, type, generic, annotations, contentType, getMetadata(), os, request.getProperties());
            writerContext.proceed();
         }
         callback.commit(); // just in case the output stream is never used
      }
      catch (Exception ex)
      {
         if (ex instanceof WriterException)
         {
            throw (WriterException) ex;
         }
         else
         {
            throw new WriterException(ex);
         }
      }
   }

   public MediaType resolveContentType()
   {
      MediaType responseContentType = null;
      Object type = getMetadata().getFirst(HttpHeaderNames.CONTENT_TYPE);
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

   public void commitHeaders(HttpResponse response)
   {
      if (getMetadata() != null)
      {
         List<Object> cookies = getMetadata().get(
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
               getMetadata().remove(HttpHeaderNames.SET_COOKIE);
         }
      }
      if (getMetadata() != null
              && getMetadata().size() > 0)
      {
         response.getOutputHeaders().putAll(getMetadata());
      }
   }

   // spec


   @Override
   public <T> T readEntity(Class<T> entityType) throws MessageProcessingException, IllegalStateException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public <T> T readEntity(GenericType<T> entityType) throws MessageProcessingException, IllegalStateException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public <T> T readEntity(Class<T> entityType, Annotation[] annotations) throws MessageProcessingException, IllegalStateException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) throws MessageProcessingException, IllegalStateException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public boolean hasEntity()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public boolean bufferEntity() throws MessageProcessingException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public void close() throws MessageProcessingException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public String getHeader(String name)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public MediaType getMediaType()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Locale getLanguage()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public int getLength()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Map<String, NewCookie> getCookies()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public EntityTag getEntityTag()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Date getDate()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Date getLastModified()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public URI getLocation()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Set<Link> getLinks()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public boolean hasLink(String relation)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Link getLink(String relation)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Link.Builder getLinkBuilder(String relation)
   {
      throw new NotImplementedYetException();
   }
}
