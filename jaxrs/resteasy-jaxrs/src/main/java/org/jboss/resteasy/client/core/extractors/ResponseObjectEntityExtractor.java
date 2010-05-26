package org.jboss.resteasy.client.core.extractors;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import javax.ws.rs.HeaderParam;

import org.jboss.resteasy.annotations.Body;
import org.jboss.resteasy.annotations.Status;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.client.core.marshallers.ResteasyClientProxy;
import org.jboss.resteasy.spi.LinkHeader;

@SuppressWarnings("unchecked")
public class ResponseObjectEntityExtractor<T> implements EntityExtractor
{
   private Class<T> returnType;
   private HashMap<Method, ResponseHandler> handlers;
   private ClientErrorHandler errorHandler;

   public ResponseObjectEntityExtractor(Method method, ClientErrorHandler errorHandler)
   {
      this.errorHandler = errorHandler;
      this.returnType = (Class<T>) method.getReturnType();
      this.handlers = new HashMap<Method, ResponseHandler>();
      for (Method interfaceMethod : this.returnType.getMethods())
      {
         this.handlers.put(interfaceMethod, createResponseHandler(interfaceMethod));
      }
   }

   protected ResponseHandler createResponseHandler(final Method method)
   {
      if (method.isAnnotationPresent(Status.class))
      {
         return new ResponseHandler()
         {
            public Object getResponseObject(ClientRequest request, BaseClientResponse response, Object... args)
            {
               return response.getStatus();
            }
         };
      }

      if (method.isAnnotationPresent(Body.class))
      {
         return new ResponseHandler()
         {
            public Object getResponseObject(ClientRequest request, BaseClientResponse response, Object... args)
            {
               return new BodyEntityExtractor(method.getReturnType(), method, null).extractEntity(request, response);
            }
         };
      }

      final HeaderParam headerParam = method.getAnnotation(HeaderParam.class);
      if (headerParam != null)
      {
         return new ResponseHandler()
         {
            public Object getResponseObject(ClientRequest request, BaseClientResponse response, Object... args)
            {
               return response.getHeaders().getFirst(headerParam.value());
            }
         };
      }

      if (method.getReturnType() == ClientRequest.class)
         return new ResponseHandler()
         {
            public Object getResponseObject(ClientRequest request, BaseClientResponse response, Object... args)
            {
               return request;
            }
         };

      if (method.getReturnType() == ClientResponse.class)
         return new ResponseHandler()
         {
            public Object getResponseObject(ClientRequest request, BaseClientResponse response, Object... args)
            {
               return response;
            }
         };

      if (method.getReturnType() == LinkHeader.class)
         return new ResponseHandler()
         {
            public Object getResponseObject(ClientRequest request, BaseClientResponse response, Object... args)
            {
               return response.getLinkHeader();
            }
         };

      if (method.isAnnotationPresent(Body.class))
         return new ResponseHandler()
         {

            public Object getResponseObject(ClientRequest request, BaseClientResponse response, Object... args)
            {
               return new BodyEntityExtractor(returnType, method, errorHandler);
            }
         };

      return null;
   }

   public Object extractEntity(ClientRequest request, BaseClientResponse clientResponse)
   {
      Class<?>[] intfs = { returnType, ResteasyClientProxy.class };
      final ClientResponseProxy clientProxy = new ClientResponseProxy(request, clientResponse, handlers, returnType);
      return Proxy.newProxyInstance(returnType.getClassLoader(), intfs, clientProxy);
   }

}
