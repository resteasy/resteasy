package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import java.io.IOException;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ExceptionMapper2Test extends BaseResourceTest
{
   @Provider
   public static class AnyExceptionExceptionMapper implements ExceptionMapper<Exception>{

      @Override
      public Response toResponse(Exception arg0) {
         Response.Status status = Response.Status.NO_CONTENT;
         if (arg0 instanceof WebApplicationException)
            return ((WebApplicationException)arg0).getResponse();
         else if (arg0 instanceof RuntimeException)
            throw new RuntimeException("CTS Test RuntimeException", arg0);
         else if (arg0 instanceof IOException)
            status = Response.Status.SERVICE_UNAVAILABLE;
         else if (arg0 != null)
            status = Response.Status.NOT_ACCEPTABLE;
         return Response.status(status).build();
      }

   }

   @Provider
   public static class IOExceptionExceptionMapper implements ExceptionMapper<IOException>{

      @Override
      public Response toResponse(IOException exception) {
         return Response.status(Response.Status.ACCEPTED).build();
      }

   }

   public enum EnumProvider {
      TCK, CTS, JAXRS;
   }

   @Provider
   public static class EnumContextResolver implements ContextResolver<EnumProvider>
   {

      @Override
      public EnumProvider getContext(Class<?> type) {
         return type == EnumProvider.class ? EnumProvider.JAXRS : null;
      }

   }

   @Provider
   @Produces(MediaType.TEXT_PLAIN)
   public static class TextPlainEnumContextResolver implements ContextResolver<EnumProvider> {
      @Override
      public EnumProvider getContext(Class<?> type) {
         return type == EnumProvider.class ? EnumProvider.CTS : null;
      }
   }





   @Path("resource")
   public static class Resource {

      @Context
      Providers providers;
      @GET
      @Path("isRegisteredRuntimeExceptionMapper")
      public Response isRegisteredRuntimeExceptionMapper() {
         ExceptionMapper<RuntimeException> em = providers
                 .getExceptionMapper(RuntimeException.class);
         Response.Status status = Response.Status.NOT_ACCEPTABLE;
         System.out.println(em.getClass().getName());
         if (em != null && em.getClass() == AnyExceptionExceptionMapper.class)
            status = Response.Status.OK;
         // This serverError() is to get ResponseBuilder with status != OK
         return Response.serverError().status(status).build();
      }

      Response getResponseByEnumProvider(EnumProvider expected, EnumProvider given) {
         Response.Status status = Response.Status.NO_CONTENT;
         if (given != null)
            status = given != expected ? Response.Status.NOT_ACCEPTABLE : Response.Status.OK;
         return Response.status(status).build();
      }

      @GET
      @Path("isRegisteredContextResolver")
      public Response isRegisteredContextResolver() {
         EnumProvider ep = getEnumProvider(MediaType.WILDCARD_TYPE);
         return getResponseByEnumProvider(EnumProvider.JAXRS, ep);
      }

      private EnumProvider getEnumProvider(MediaType type) {
         ContextResolver<EnumProvider> scr = providers.getContextResolver(
                 EnumProvider.class, type);
         EnumProvider ep = scr.getContext(EnumProvider.class);
         return ep;
      }



   }

   static Client client;

   @BeforeClass
   public static void setup()
   {
      deployment.getProviderFactory().register(AnyExceptionExceptionMapper.class);
      deployment.getProviderFactory().register(IOExceptionExceptionMapper.class);
      deployment.getProviderFactory().register(EnumContextResolver.class);
      deployment.getProviderFactory().register(TextPlainEnumContextResolver.class);
      addPerRequestResource(Resource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }

   @Test
   public void isRegisteredWildCardContextResolverTest()
   {
      Response response = client.target(generateURL("/resource/isRegisteredContextResolver")).request().get();
      Assert.assertEquals(response.getStatus(), 200);
      response.close();
   }


   @Test
   public void testExceptionMapped()
   {
      Response response = client.target(generateURL("/resource/isRegisteredRuntimeExceptionMapper")).request().get();
      Assert.assertEquals(response.getStatus(), 200);
      response.close();
   }


}
