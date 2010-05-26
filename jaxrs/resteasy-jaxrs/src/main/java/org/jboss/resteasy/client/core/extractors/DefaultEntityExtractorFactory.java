package org.jboss.resteasy.client.core.extractors;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.annotations.ClientResponseType;
import org.jboss.resteasy.annotations.ResponseObject;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.EntityTypeFactory;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.util.Types;

@SuppressWarnings("unchecked")
public class DefaultEntityExtractorFactory implements EntityExtractorFactory
{

   public EntityExtractor createExtractor(final Method method, final ClientErrorHandler errorHandler)
   {
      final Class returnType = method.getReturnType();
      if (ClientResponse.class.isAssignableFrom(returnType))
      {
         final Type methodGenericReturnType = method.getGenericReturnType();

         if (methodGenericReturnType instanceof ParameterizedType)
            return createParameterizedTypeExtractor(methodGenericReturnType);
         else
            return clientResponseExtractor();
      }

      if (returnType.equals(Response.Status.class))
      {
         return createStatusExtractor();
      }

      if (Response.class.isAssignableFrom(returnType))
      {

         final ClientResponseType responseHint = method.getAnnotation(ClientResponseType.class);
         if (responseHint != null)
         {
            final Class responseHintReturnType = responseHint.entityType();
            if (ClientExtractorUtility.isVoidReturnType(responseHintReturnType))
            {
               final Class<? extends EntityTypeFactory> entityTypeFactory = responseHint.entityTypeFactory();
               return new EntityExtractor()
               {
                  public Object extractEntity(ClientRequest request, BaseClientResponse clientResponse)
                  {

                     EntityTypeFactory factory = null;
                     try
                     {
                        factory = entityTypeFactory.newInstance();
                     }
                     catch (InstantiationException e)
                     {
                        throw clientResponse
                              .createResponseFailure("Could not create a default entity type factory of type "
                                    + entityTypeFactory.getClass().getName());
                     }
                     catch (IllegalAccessException e)
                     {
                        throw clientResponse
                              .createResponseFailure("Could not create a default entity type factory of type "
                                    + entityTypeFactory.getClass().getName() + ". " + e.getMessage());
                     }
                     clientResponse.setReturnType(factory.getEntityType(clientResponse.getStatus(), clientResponse
                           .getMetadata()));
                     return clientResponse;
                  }
               };
            }
            else
            {
               return new EntityExtractor()
               {
                  public Object extractEntity(ClientRequest request, BaseClientResponse clientResponse)
                  {
                     clientResponse.setReturnType(responseHintReturnType);
                     return clientResponse;
                  }
               };
            }
         }
         else
         {
            return clientResponseExtractor();
         }
      }

      if (returnType.isInterface() && returnType.isAnnotationPresent(ResponseObject.class))
      {
         return new ResponseObjectEntityExtractor(method, errorHandler);
      }

      // We are not a ClientResponse type so we need to unmarshall and narrow it
      // to right type. If we are unable to unmarshall, or encounter any kind of
      // Exception, give the ClientErrorHandlers a chance to handle the
      // ClientResponse manually.

      return new BodyEntityExtractor(returnType, method, errorHandler);
   }

   private EntityExtractor createStatusExtractor()
   {
      return new EntityExtractor<Response.Status>()
      {
         public Status extractEntity(ClientRequest request, BaseClientResponse<Status> clientResponse)
         {
            clientResponse.releaseConnection();
            return clientResponse.getResponseStatus();
         }
      };
   }

   protected EntityExtractor createParameterizedTypeExtractor(final Type methodGenericReturnType)
   {
      final ParameterizedType zType = (ParameterizedType) methodGenericReturnType;
      final Type genericReturnType = zType.getActualTypeArguments()[0];
      final Class<?> responseReturnType = Types.getRawType(genericReturnType);
      return new EntityExtractor()
      {
         public Object extractEntity(ClientRequest request, BaseClientResponse clientResponse)
         {
            clientResponse.setReturnType(responseReturnType);
            clientResponse.setGenericReturnType(genericReturnType);
            return clientResponse;
         }
      };
   }

   private static EntityExtractor clientResponseExtractor()
   {
      return new EntityExtractor<ClientResponse>()
      {
         public ClientResponse extractEntity(ClientRequest request, BaseClientResponse<ClientResponse> clientResponse)
         {
            return clientResponse;
         }
      };
   }
}
