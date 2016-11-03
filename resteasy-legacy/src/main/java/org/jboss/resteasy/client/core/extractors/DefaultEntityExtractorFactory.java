package org.jboss.resteasy.client.core.extractors;

import org.jboss.resteasy.annotations.legacy.ClientResponseType;
import org.jboss.resteasy.annotations.ResponseObject;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.EntityTypeFactory;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.core.Response;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author Solomon.Duskis
 */
@SuppressWarnings("unchecked")
public class DefaultEntityExtractorFactory implements EntityExtractorFactory
{

   public static final EntityExtractor clientResponseExtractor = new EntityExtractor<ClientResponse>()
   {
      public ClientResponse extractEntity(ClientRequestContext context, Object... args)
      {
         return context.getClientResponse();
      }
   };

   public static EntityExtractor<Response.Status> createStatusExtractor(final boolean release)
   {
      return new EntityExtractor<Response.Status>()
      {
         public Response.Status extractEntity(ClientRequestContext context, Object... args)
         {
            if (release)
               context.getClientResponse().releaseConnection();
            return context.getClientResponse().getResponseStatus();
         }
      };
   }

   public static final EntityExtractor createVoidExtractor(final boolean release)
   {
      return new EntityExtractor()
      {
         public Object extractEntity(ClientRequestContext context, Object... args)
         {
            try
            {
               context.getClientResponse().checkFailureStatus();
            }
            catch (ClientResponseFailure ce)
            {
               // If ClientResponseFailure do a copy of the response and then release the connection,
               // we need to use the copy here and not the original response
               context.getErrorHandler().clientErrorHandling((BaseClientResponse) ce.getResponse(), ce);
            }
            catch (RuntimeException e)
            {
               context.getErrorHandler().clientErrorHandling(context.getClientResponse(), e);
            }
            if (release)
               context.getClientResponse().releaseConnection();
            return null;
         }
      };
   }

   public EntityExtractor createExtractor(final Method method)
   {
      final Class returnType = method.getReturnType();
      if (isVoidReturnType(returnType))
         return createVoidExtractor(true);
      if (returnType.equals(Response.Status.class))
         return createStatusExtractor(true);

      if (Response.class.isAssignableFrom(returnType))
         return createResponseTypeEntityExtractor(method);

      if (returnType.isInterface() && returnType.isAnnotationPresent(ResponseObject.class))
         return new ResponseObjectProxy(method, new ResponseObjectEntityExtractorFactory());

      // We are not a ClientResponse type so we need to unmarshall and narrow it
      // to right type. If we are unable to unmarshall, or encounter any kind of
      // Exception, give the ClientErrorHandlers a chance to handle the
      // ClientResponse manually.

      return new BodyEntityExtractor(method);
   }

   protected EntityExtractor createResponseTypeEntityExtractor(final Method method)
   {
      final ClientResponseType responseHint = method.getAnnotation(ClientResponseType.class);
      if (responseHint != null)
      {
         final Class responseHintReturnType = responseHint.entityType();
         if (isVoidReturnType(responseHintReturnType))
         {
            final Class<? extends EntityTypeFactory> entityTypeFactory = responseHint.entityTypeFactory();
            return new EntityExtractor()
            {
               public Object extractEntity(ClientRequestContext context, Object... args)
               {
                  EntityTypeFactory factory = null;
                  try
                  {
                     factory = entityTypeFactory.newInstance();
                  }
                  catch (InstantiationException e)
                  {
                     throw context.getClientResponse().createResponseFailure(Messages.MESSAGES.couldNotCreateEntityFactory(entityTypeFactory.getClass().getName()));
                  }
                  catch (IllegalAccessException e)
                  {
                     throw ((BaseClientResponse<?>)context.getClientResponse())
                           .createResponseFailure(Messages.MESSAGES.couldNotCreateEntityFactoryMessage(entityTypeFactory.getClass().getName(), e.getMessage()));
                  }
                  context.getClientResponse().setReturnType(
                          factory.getEntityType(((BaseClientResponse<?>) context.getClientResponse()).getStatus(),
                                  ((BaseClientResponse<?>) context.getClientResponse()).getMetadata()));
                  return context.getClientResponse();
               }
            };
         }
         else
         {
            return new EntityExtractor()
            {
               public Object extractEntity(ClientRequestContext context, Object... args)
               {
                  context.getClientResponse().setReturnType(responseHintReturnType);
                  return context.getClientResponse();
               }
            };
         }
      }
      else
      {
         final Type methodGenericReturnType = method.getGenericReturnType();
         if (methodGenericReturnType instanceof ParameterizedType)
         {
            final ParameterizedType zType = (ParameterizedType) methodGenericReturnType;
            final Type genericReturnType = zType.getActualTypeArguments()[0];
            final Class<?> responseReturnType = Types.getRawType(genericReturnType);
            return new EntityExtractor()
            {
               public Object extractEntity(ClientRequestContext context, Object... args)
               {
                  context.getClientResponse().setReturnType(responseReturnType);
                  context.getClientResponse().setGenericReturnType(genericReturnType);
                  return context.getClientResponse();
               }
            };
         }
         else
            return clientResponseExtractor;
      }
   }

   public static final boolean isVoidReturnType(Class<?> returnType)
   {
      return returnType == null || void.class.equals(returnType) || Void.class.equals(returnType);
   }

}
