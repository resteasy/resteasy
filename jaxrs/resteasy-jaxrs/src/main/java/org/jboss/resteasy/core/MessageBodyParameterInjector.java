package org.jboss.resteasy.core;

import org.jboss.resteasy.core.interception.InterceptorRegistry;
import org.jboss.resteasy.core.interception.InterceptorRegistryListener;
import org.jboss.resteasy.core.messagebody.ReaderUtility;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.MessageBodyReaderInterceptor;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.ThreadLocalStack;

import javax.ws.rs.Encoded;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
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
   private ReaderUtility readerUtility;
   private ResteasyProviderFactory factory;
   private Class declaringClass;
   private AccessibleObject target;

   private class ReaderUtilityImpl extends ReaderUtility
   {
      private ReaderUtilityImpl(ResteasyProviderFactory factory, MessageBodyReaderInterceptor[] interceptors)
      {
         super(factory, interceptors);
      }


      public RuntimeException createReaderNotFound(Type genericType, MediaType mediaType)
      {
         return new BadRequestException(
                 "Could not find message body reader for type: "
                         + genericType + " of content type: " + mediaType);
      }
   }

   public MessageBodyParameterInjector(Class declaringClass, AccessibleObject target, Class type, Type genericType, Annotation[] annotations, ResteasyProviderFactory factory)
   {
      this.factory = factory;
      this.declaringClass = declaringClass;
      this.target = target;
      this.type = type;
      this.genericType = genericType;
      this.annotations = annotations;
      MessageBodyReaderInterceptor[] interceptors = factory
              .getServerMessageBodyReaderInterceptorRegistry().bind(
                      declaringClass, target);
      this.readerUtility = new ReaderUtilityImpl(factory, interceptors);

      // this is for when an interceptor is added after the creation of the injector
      factory.getServerMessageBodyReaderInterceptorRegistry().getListeners().add(this);
   }

   public void registryUpdated(InterceptorRegistry registry)
   {
      MessageBodyReaderInterceptor[] interceptors = factory
              .getServerMessageBodyReaderInterceptorRegistry().bind(
                      declaringClass, target);
      this.readerUtility = new ReaderUtilityImpl(factory, interceptors);
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
      try
      {
         Object o = getBody();
         if (o != null)
         {
            return o;
         }
         final MediaType mediaType = request.getHttpHeaders().getMediaType();
         if (mediaType == null)
         {
            throw new BadRequestException("content-type was null and expecting to extract a body");
         }

         // We have to do this hack because of servlets and servlet filters
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
            return readerUtility.doRead(request, type, genericType, annotations, mediaType);
         }
      }
      catch (IOException e)
      {
         throw new BadRequestException("Failure extracting body", e);
      }
   }

   public Object inject()
   {
      throw new RuntimeException("Illegal to inject a message body into a singleton");
   }
}
