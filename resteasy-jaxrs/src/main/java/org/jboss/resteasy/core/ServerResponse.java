package org.jboss.resteasy.core;

import org.jboss.resteasy.core.interception.MessageBodyWriterContextImpl;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.WriterException;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServerResponse extends Response
{
   protected Object entity;
   protected int status = HttpResponseCodes.SC_OK;
   protected Headers<Object> metadata = new Headers<Object>();
   protected Annotation[] annotations;
   protected Type genericType;
   protected PostProcessInterceptor[] postProcessInterceptors;
   protected MessageBodyWriterInterceptor[] messageBodyWriterInterceptors;

   public ServerResponse(Object entity, int status, Headers<Object> metadata)
   {
      this.entity = entity;
      this.status = status;
      this.metadata = metadata;
   }

   public ServerResponse()
   {
   }

   public static ServerResponse copyIfNotServerResponse(Response response)
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

   public MessageBodyWriterInterceptor[] getMessageBodyWriterInterceptors()
   {
      return messageBodyWriterInterceptors;
   }

   public void setMessageBodyWriterInterceptors(MessageBodyWriterInterceptor[] messageBodyWriterInterceptors)
   {
      this.messageBodyWriterInterceptors = messageBodyWriterInterceptors;
   }

   public PostProcessInterceptor[] getPostProcessInterceptors()
   {
      return postProcessInterceptors;
   }

   public void setPostProcessInterceptors(PostProcessInterceptor[] postProcessInterceptors)
   {
      this.postProcessInterceptors = postProcessInterceptors;
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

   public void writeTo(HttpResponse response, ResteasyProviderFactory providerFactory) throws WriterException
   {
      if (postProcessInterceptors != null)
      {
         for (PostProcessInterceptor interceptor : postProcessInterceptors)
         {
            interceptor.postProcess(this);
         }
      }
      if (entity == null)
      {
         outputHeaders(response);
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
         outputHeaders(response);
         long size = writer.getSize(ent, type, generic, annotations, contentType);
         if (size > -1) response.getOutputHeaders().putSingle(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(size));


         if (messageBodyWriterInterceptors == null || messageBodyWriterInterceptors.length == 0)
         {
            writer.writeTo(ent, type, generic, annotations,
                    contentType, response.getOutputHeaders(), response
                            .getOutputStream());
         }
         else
         {
            MessageBodyWriterContextImpl ctx = new MessageBodyWriterContextImpl(messageBodyWriterInterceptors, writer, ent, type, generic,
                    annotations, contentType, response.getOutputHeaders(), response.getOutputStream());
            ctx.proceed();
         }
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

   public void outputHeaders(HttpResponse response)
   {
      // Let servlet container set cookies
      response.setStatus(getStatus());
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

}
