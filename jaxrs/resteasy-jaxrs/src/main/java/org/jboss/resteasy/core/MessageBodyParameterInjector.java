package org.jboss.resteasy.core;

import org.jboss.resteasy.core.interception.InterceptorRegistry;
import org.jboss.resteasy.core.interception.InterceptorRegistryListener;
import org.jboss.resteasy.core.interception.ServerMessageBodyReaderContext;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.MarshalledEntity;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.MessageBodyReaderInterceptor;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.InputStreamToByteArray;
import org.jboss.resteasy.util.ThreadLocalStack;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.Encoded;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class MessageBodyParameterInjector implements ValueInjector, InterceptorRegistryListener
{
   private static ThreadLocalStack<Object> bodyStack = new ThreadLocalStack<Object>();

   public static void pushBody(Object o)
   {
      bodyStack.push(o);
   }

   public static Object getBody()
   {
      return bodyStack.get();
   }

   public static Object popBody()
   {
      return bodyStack.pop();
   }

   public static int bodyCount()
   {
      return bodyStack.size();
   }

   public static void clearBodies()
   {
      bodyStack.clear();
   }

   private Class type;
   private Type genericType;
   private Annotation[] annotations;
   private ResteasyProviderFactory factory;
   private Class declaringClass;
   private AccessibleObject target;
   private MessageBodyReaderInterceptor[] interceptors;
   private boolean isMarshalledEntity;

   public MessageBodyParameterInjector(Class declaringClass, AccessibleObject target, Class type, Type genericType, Annotation[] annotations, ResteasyProviderFactory factory)
   {
      this.factory = factory;
      this.target = target;
      this.declaringClass = declaringClass;

      if (type.equals(MarshalledEntity.class))
      {
         if (genericType == null || !(genericType instanceof ParameterizedType))
         {
            throw new RuntimeException("MarshalledEntity must have type information.");
         }
         isMarshalledEntity = true;
         ParameterizedType param = (ParameterizedType) genericType;
         this.genericType = param.getActualTypeArguments()[0];
         this.type = Types.getRawType(this.genericType);
      }
      else
      {
         this.type = type;
         this.genericType = genericType;
      }
      this.annotations = annotations;
      this.interceptors = factory
              .getServerMessageBodyReaderInterceptorRegistry().bind(
                      this.declaringClass, this.target);

      // this is for when an interceptor is added after the creation of the injector
      factory.getServerMessageBodyReaderInterceptorRegistry().getListeners().add(this);
   }

   public void registryUpdated(InterceptorRegistry registry)
   {
      this.interceptors = factory
              .getServerMessageBodyReaderInterceptorRegistry().bind(
                      declaringClass, target);
   }

   public boolean isFormData(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      if (!mediaType.equals(MediaType.APPLICATION_FORM_URLENCODED_TYPE)) return false;
      if (!MultivaluedMap.class.isAssignableFrom(type)) return false;
      if (genericType == null) return true;

      if (!(genericType instanceof ParameterizedType)) return false;
      ParameterizedType params = (ParameterizedType) genericType;
      if (params.getActualTypeArguments().length != 2) return false;
      return params.getActualTypeArguments()[0].equals(String.class) && params.getActualTypeArguments()[1].equals(String.class);
   }


   public Object inject(HttpRequest request, HttpResponse response)
   {
      Object o = getBody();
      if (o != null)
      {
         return o;
      }
      MediaType mediaType = request.getHttpHeaders().getMediaType();
      if (mediaType == null)
      {
         mediaType = MediaType.WILDCARD_TYPE;
         //throw new BadRequestException("content-type was null and expecting to extract a body into " + this.target);
      }

      // We have to do this isFormData() hack because of servlets and servlet filters
      // A filter that does getParameter() will screw up the input stream which will screw up the
      // provider.  We do it here rather than hack the provider as the provider is reused for client side
      // and also, the server may be using the client framework to make another remote call.
      if (isFormData(type, genericType, annotations, mediaType))
      {
         boolean encoded = FindAnnotation.findAnnotation(annotations, Encoded.class) != null;
         if (encoded) return request.getFormParameters();
         else return request.getDecodedFormParameters();
      }
      else
      {
         MessageBodyReader reader = factory.getMessageBodyReader(type,
                 genericType, annotations, mediaType);
         if (reader == null)
         {
            return new BadRequestException(
                    "Could not find message body reader for type: "
                            + genericType + " of content type: " + mediaType);
         }

         try
         {
            InputStream is = request.getInputStream();
            if (isMarshalledEntity)
            {
               is = new InputStreamToByteArray(is);

            }
            ServerMessageBodyReaderContext messageBodyReaderContext = new ServerMessageBodyReaderContext(interceptors, reader, type,
                    genericType, annotations, mediaType, request
                    .getHttpHeaders().getRequestHeaders(), is, request);
            final Object obj = messageBodyReaderContext.proceed();
            if (isMarshalledEntity)
            {
               InputStreamToByteArray isba = (InputStreamToByteArray) is;
               final byte[] bytes = isba.toByteArray();
               return new MarshalledEntity()
               {
                  @Override
                  public byte[] getMarshalledBytes()
                  {
                     return bytes;
                  }

                  @Override
                  public Object getEntity()
                  {
                     return obj;
                  }
               };
            }
            else
            {
               return obj;
            }
         }
         catch (Exception e)
         {
            if (e instanceof ReaderException)
            {
               throw (ReaderException) e;
            }
            else
            {
               throw new ReaderException(e);
            }
         }
      }
   }

   public Object inject()
   {
      throw new RuntimeException("Illegal to inject a message body into a singleton into " + this.target);
   }
}
