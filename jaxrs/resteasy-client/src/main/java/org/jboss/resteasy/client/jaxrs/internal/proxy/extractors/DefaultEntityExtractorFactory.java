package org.jboss.resteasy.client.jaxrs.internal.proxy.extractors;

import org.jboss.resteasy.annotations.ResponseObject;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;

import javax.ws.rs.core.Response;
import java.lang.reflect.Method;

/**
 * @author Solomon.Duskis
 */
@SuppressWarnings("unchecked")
public class DefaultEntityExtractorFactory implements EntityExtractorFactory
{

   public static final EntityExtractor clientResponseExtractor = new EntityExtractor<ClientResponse>()
   {
      public ClientResponse extractEntity(ClientContext context, Object... args)
      {
         return context.getClientResponse();
      }
   };

   public static EntityExtractor<Response.Status> createStatusExtractor(final boolean release)
   {
      return new EntityExtractor<Response.Status>()
      {
         public Response.Status extractEntity(ClientContext context, Object... args)
         {
            if (release)
               context.getClientResponse().close();
            return Response.Status.fromStatusCode(context.getClientResponse().getStatus());
         }
      };
   }

   public static final EntityExtractor createVoidExtractor()
   {
      return new EntityExtractor()
      {
         public Object extractEntity(ClientContext context, Object... args)
         {
            ClientResponse response = context.getClientResponse();
            int status = response.getStatus();
            if (status >= 400)
            {
               response.bufferEntity();
               response.close();
               ClientInvocation.handleErrorStatus(response);
            }
            response.close();
            return null;
         }
      };
   }

   public EntityExtractor createExtractor(final Method method)
   {
      final Class returnType = method.getReturnType();
      if (isVoidReturnType(returnType))
         return createVoidExtractor();
      if (returnType.equals(Response.Status.class))
         return createStatusExtractor(true);

      if (Response.class.isAssignableFrom(returnType))
         return clientResponseExtractor;

      if (returnType.isInterface() && returnType.isAnnotationPresent(ResponseObject.class))
         return new ResponseObjectProxy(method, new ResponseObjectEntityExtractorFactory());

      return new BodyEntityExtractor(method);
   }

   protected EntityExtractor createResponseTypeEntityExtractor(final Method method)
   {
      return new EntityExtractor<Response>() {
         @Override
         public Response extractEntity(ClientContext context, Object... args)
         {
            return context.getClientResponse();
         }
      };
   }

   public static final boolean isVoidReturnType(Class<?> returnType)
   {
      return returnType == null || void.class.equals(returnType) || Void.class.equals(returnType);
   }

}
