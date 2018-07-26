package org.jboss.resteasy.test.providers.sse;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class SseCORSFilterTest
{
   @Deployment
   public static Archive<?> deploy()
   {
      WebArchive war = TestUtil.prepareArchive(SseCORSFilterTest.class.getSimpleName());
      war.addClass(SseCORSFilterTest.class);
      war.addAsWebInfResource("org/jboss/resteasy/test/providers/sse/filter/web.xml", "web.xml");
      war.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
              new RuntimePermission("modifyThread")
      ), "permissions.xml");
      return TestUtil.finishContainerPrepare(war, null, SseFilterApplication.class, SseResource.class,
            CORSFilter.class, ExecutorServletContextListener.class);
   }

   private String generateURL(String path)
   {
      return PortProviderUtil.generateURL(path, SseCORSFilterTest.class.getSimpleName());
   }

   @Test
   public void testSseEventWithFilter() throws Exception
   {

      Client client = ClientBuilder.newClient();
      WebTarget target = client.target(generateURL("/service/server-sent-events/events"));
      Response response = target.request().get();
      Assert.assertEquals("response OK is expected", 200, response.getStatus());
      Assert.assertTrue("CORS http header is expected in event response",
            response.getHeaders().get("Access-Control-Allow-Origin").contains("*"));
      MediaType mt = response.getMediaType();
      mt = new MediaType(mt.getType(), mt.getSubtype());
      Assert.assertEquals("text/event-stream is expected", mt, MediaType.SERVER_SENT_EVENTS_TYPE);

      Client isOpenClient = ClientBuilder.newClient();
      Invocation.Builder isOpenRequest = isOpenClient.target(generateURL("/service/server-sent-events/isopen"))
            .request();
      javax.ws.rs.core.Response isOpenResponse = isOpenRequest.get();
      Assert.assertTrue("EventSink open is expected ", isOpenResponse.readEntity(Boolean.class));
      Assert.assertTrue("CORS http header is expected in isOpenResponse",
            isOpenResponse.getHeaders().get("Access-Control-Allow-Origin").contains("*"));
      isOpenClient.close();
      client.close();
   }
}
