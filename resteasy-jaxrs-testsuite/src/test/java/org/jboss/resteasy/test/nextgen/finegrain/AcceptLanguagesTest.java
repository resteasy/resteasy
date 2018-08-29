package org.jboss.resteasy.test.nextgen.finegrain;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.util.List;
import java.util.Locale;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AcceptLanguagesTest extends BaseResourceTest
{

   private static final Logger LOG = Logger.getLogger(AcceptLanguagesTest.class);

   @Path("/lang")
   public static class Accept
   {

      @GET
      @Produces("text/plain")
      public String get(@Context HttpHeaders headers)
      {
         // en-US;q=0,en;q=0.8,de-AT,de;q=0.9
         List<Locale> accepts = headers.getAcceptableLanguages();
         for (Locale locale : accepts)
         {
            LOG.info(locale);
         }
         Assert.assertEquals(accepts.get(0).toString(), "de_AT");
         Assert.assertEquals(accepts.get(1).toString(), "de");
         Assert.assertEquals(accepts.get(2).toString(), "en");
         Assert.assertEquals(accepts.get(3).toString(), "en_US");
         return "hello";
      }
   }

   @BeforeClass
   public static void setUp() throws Exception
   {
      deployment.getRegistry().addPerRequestResource(Accept.class);
   }

   @Test
   public void testMe() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/lang"));
      Invocation.Builder request = target.request().header("Accept-Language", "en-US;q=0,en;q=0.8,de-AT,de;q=0.9");
      Assert.assertEquals(request.get().getStatus(), 200);
      client.close();

   }

}
