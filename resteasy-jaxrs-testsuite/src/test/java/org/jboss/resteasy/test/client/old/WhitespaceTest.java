package org.jboss.resteasy.test.client.old;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.List;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class WhitespaceTest extends BaseResourceTest
{

   private static final Logger LOG = Logger.getLogger(WhitespaceTest.class);

   @Resource
   @Path(value = "/sayhello")
   public static class Hello
   {

      @Context
      UriInfo info;

      @GET
      @Path("/en/{in}")
      @Produces("text/plain")
      public String echo(@PathParam(value = "in") String in)
      {
         Assert.assertEquals(SPACES_REQUEST, in);
         List<String> params = info.getPathParameters(true).get("in");
         LOG.info("DECODE" + params.get(0));

         params = info.getPathParameters(false).get("in");
         LOG.info("ENCODE" + params.get(0));


         return in;
      }


   }


   @Path(value = "/sayhello")
   public interface HelloClient
   {

      @GET
      @Path("/en/{in}")
      @Produces("text/plain")
      String sayHi(@PathParam(value = "in") String in);


   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(Hello.class);
   }


   private static final String SPACES_REQUEST = "something something";

   @Test
   public void testEcho()
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      HelloClient client = ProxyFactory.create(HelloClient.class, generateBaseUrl());
      Assert.assertEquals(SPACES_REQUEST, client.sayHi(SPACES_REQUEST));
   }


}
