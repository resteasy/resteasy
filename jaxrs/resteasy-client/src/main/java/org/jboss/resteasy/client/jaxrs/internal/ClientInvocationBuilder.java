package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientInvocationBuilder implements Invocation.Builder
{
   protected ClientInvocation invocation;

   public ClientInvocationBuilder(ResteasyClient client, URI uri, ClientConfiguration configuration)
   {
      invocation = new ClientInvocation(client, uri, new ClientRequestHeaders(configuration), configuration);
   }

   public ClientInvocation getInvocation()
   {
      return invocation;
   }

   public ClientRequestHeaders getHeaders()
   {
      return invocation.headers;
   }

   @Override
   public Invocation.Builder accept(String... mediaTypes)
   {
      getHeaders().accept(mediaTypes);
      return this;
   }

   @Override
   public Invocation.Builder accept(MediaType... mediaTypes)
   {
      getHeaders().accept(mediaTypes);
      return this;
   }

   @Override
   public Invocation.Builder acceptLanguage(Locale... locales)
   {
      getHeaders().acceptLanguage(locales);
      return this;
   }

   @Override
   public Invocation.Builder acceptLanguage(String... locales)
   {
      getHeaders().acceptLanguage(locales);
      return this;
   }

   @Override
   public Invocation.Builder acceptEncoding(String... encodings)
   {
      getHeaders().acceptEncoding(encodings);
      return this;
   }

   @Override
   public Invocation.Builder cookie(Cookie cookie)
   {
      getHeaders().cookie(cookie);
      return this;
   }

   @Override
   public Invocation.Builder cookie(String name, String value)
   {
      getHeaders().cookie(new Cookie(name, value));
      return this;
   }

   @Override
   public Invocation.Builder cacheControl(CacheControl cacheControl)
   {
      getHeaders().cacheControl(cacheControl);
      return this;
   }

   @Override
   public Invocation.Builder header(String name, Object value)
   {
      getHeaders().header(name, value);
      return this;
   }

   @Override
   public Invocation.Builder headers(MultivaluedMap<String, Object> headers)
   {
      getHeaders().setHeaders(headers);
      return this;
   }

   @Override
   public Invocation build(String method)
   {
      invocation.setMethod(method);
      return invocation;
   }

   @Override
   public Invocation build(String method, Entity<?> entity)
   {
      invocation.setMethod(method);
      invocation.setEntity(entity);
      return invocation;
   }

   @Override
   public Invocation buildGet()
   {
      return build(HttpMethod.GET);
   }

   @Override
   public Invocation buildDelete()
   {
      return build(HttpMethod.DELETE);
   }

   @Override
   public Invocation buildPost(Entity<?> entity)
   {
      return build(HttpMethod.POST, entity);
   }

   @Override
   public Invocation buildPut(Entity<?> entity)
   {
      return build(HttpMethod.PUT, entity);
   }

   @Override
   public AsyncInvoker async()
   {
      return new AsynchronousInvoke(invocation);
   }

   @Override
   public Response get()
   {
      return buildGet().invoke();
   }

   /**
    * Extracts result from response throwing an appropriate exception if not a successful response.
    *
    * @param responseType
    * @param response
    * @param annotations
    * @param <T>
    * @return
    */
   public static <T> T extractResult(GenericType<T> responseType, Response response, Annotation[] annotations)
   {
      int status = response.getStatus();
      if (status >= 200 && status < 300)
      {
         try
         {
            if (response.getMediaType() == null)
            {
               return null;
            }
            else
            {
               return response.readEntity(responseType, annotations);
            }
         }
         catch (WebApplicationException wae)
         {
            throw wae;
         }
         catch (Throwable throwable)
         {
            throw new ResponseProcessingException(response, throwable);
         }
         finally
         {
            if (response.getMediaType() == null) response.close();
         }
      }
      try
      {
         if (status >= 300 && status < 400) throw new RedirectionException(response);

         return handleErrorStatus(response);
      }
      finally
      {
         // close if no content
         if (response.getMediaType() == null) response.close();
      }

   }

   /**
    * Throw an exception.  Expecting a status of 400 or greater.
    *
    * @param response
    * @param <T>
    * @return
    */
   public static <T> T handleErrorStatus(Response response)
   {
      final int status = response.getStatus();
      switch (status)
      {
         case 400:
            throw new BadRequestException(response);
         case 401:
            throw new NotAuthorizedException(response);
         case 404:
            throw new NotFoundException(response);
         case 405:
            throw new NotAllowedException(response);
         case 406:
            throw new NotAcceptableException(response);
         case 415:
            throw new NotSupportedException(response);
         case 500:
            throw new InternalServerErrorException(response);
         case 503:
            throw new ServiceUnavailableException(response);
         default:
            break;
      }

      if (status >= 400 && status < 500) throw new ClientErrorException(response);
      if (status >= 500) throw new ServerErrorException(response);


      throw new WebApplicationException(response);
   }

   @Override
   public <T> T get(Class<T> responseType)
   {
      Response response = get();
      return extractResult(new GenericType<T>(responseType), response, null);
   }

   @Override
   public <T> T get(GenericType<T> responseType)
   {
      return extractResult(responseType, get(), null);
   }

   @Override
   public Response put(Entity<?> entity)
   {
      return buildPut(entity).invoke();
   }

   @Override
   public <T> T put(Entity<?> entity, Class<T> responseType)
   {
      Response response = put(entity);
      return extractResult(new GenericType<T>(responseType), response, null);
   }

   @Override
   public <T> T put(Entity<?> entity, GenericType<T> responseType)
   {
      Response response = put(entity);
      return extractResult(responseType, response, null);
   }

   @Override
   public Response post(Entity<?> entity)
   {
      return buildPost(entity).invoke();
   }

   @Override
   public <T> T post(Entity<?> entity, Class<T> responseType)
   {
      Response response = post(entity);
      return extractResult(new GenericType<T>(responseType), response, null);
   }

   @Override
   public <T> T post(Entity<?> entity, GenericType<T> responseType)
   {
      Response response = post(entity);
      return extractResult(responseType, response, null);
   }

   @Override
   public Response delete()
   {
      return buildDelete().invoke();
   }

   @Override
   public <T> T delete(Class<T> responseType)
   {
      Response response = delete();
      return extractResult(new GenericType<T>(responseType), response, null);
   }

   @Override
   public <T> T delete(GenericType<T> responseType)
   {
      Response response = delete();
      return extractResult(responseType, response, null);
   }

   @Override
   public Response head()
   {
      return build(HttpMethod.HEAD).invoke();
   }

   @Override
   public Response options()
   {
      return build(HttpMethod.OPTIONS).invoke();
   }

   @Override
   public <T> T options(Class<T> responseType)
   {
      Response response = options();
      return extractResult(new GenericType<T>(responseType), response, null);
   }

   @Override
   public <T> T options(GenericType<T> responseType)
   {
      Response response = options();
      return extractResult(responseType, response, null);
   }

   @Override
   public Response trace()
   {
      return build("TRACE").invoke();
   }

   @Override
   public <T> T trace(Class<T> responseType)
   {
      Response response = trace();
      return extractResult(new GenericType<T>(responseType), response, null);
   }

   @Override
   public <T> T trace(GenericType<T> responseType)
   {
      Response response = trace();
      return extractResult(responseType, response, null);
   }

   @Override
   public Response method(String name)
   {
      return build(name).invoke();
   }

   @Override
   public <T> T method(String name, Class<T> responseType)
   {
      Response response = method(name);
      return extractResult(new GenericType<T>(responseType), response, null);
   }

   @Override
   public <T> T method(String name, GenericType<T> responseType)
   {
      Response response = method(name);
      return extractResult(responseType, response, null);
   }

   @Override
   public Response method(String name, Entity<?> entity)
   {
      return build(name, entity).invoke();
   }

   @Override
   public <T> T method(String name, Entity<?> entity, Class<T> responseType)
   {
      Response response = method(name, entity);
      return extractResult(new GenericType<T>(responseType), response, null);
   }

   @Override
   public <T> T method(String name, Entity<?> entity, GenericType<T> responseType)
   {
      Response response = method(name, entity);
      return extractResult(responseType, response, null);
   }

   @Override
   public Invocation.Builder property(String name, Object value)
   {
      invocation.property(name, value);
      return this;
   }
}
