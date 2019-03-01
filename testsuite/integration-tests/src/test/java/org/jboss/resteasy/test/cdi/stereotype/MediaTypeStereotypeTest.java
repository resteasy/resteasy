package org.jboss.resteasy.test.cdi.stereotype;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.cdi.stereotype.resource.MediaTypeResource;
import org.jboss.resteasy.test.cdi.stereotype.resource.MethodMediaTypeResource;
import org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes.MediaTypeStereotype;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests media type definitions taken from CDI streotypes
 * @tpSince RESTEasy 4.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MediaTypeStereotypeTest
{
   private static final String DEPLOY_CLASS = "deployClass";
   private static final String DEPLOY_METHOD = "deployMethod";

   private static Client client;

   @BeforeClass
   public static void setup()
   {
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void close()
   {
      client.close();
   }

   @Deployment(name = DEPLOY_CLASS)
   private static Archive<?> deploy()
   {
      WebArchive war = TestUtil.prepareArchive(DEPLOY_CLASS);
      war.addClasses(MediaTypeStereotype.class);
      return TestUtil.finishContainerPrepare(war, null, MediaTypeResource.class);
   }

   @Deployment(name = DEPLOY_METHOD)
   private static Archive<?> deployMethod()
   {
      WebArchive war = TestUtil.prepareArchive(DEPLOY_METHOD);
      war.addClasses(MediaTypeStereotype.class);
      return TestUtil.finishContainerPrepare(war, null, MethodMediaTypeResource.class);
   }

   @Test
   @OperateOnDeployment(DEPLOY_CLASS)
   public void testProducesInStereotype()
   {
      testProduces(DEPLOY_CLASS);
   }

   @Test
   @OperateOnDeployment(DEPLOY_CLASS)
   public void testConsumesInStereotype()
   {
      testConsumes(DEPLOY_CLASS);
   }

   @Test
   @OperateOnDeployment(DEPLOY_METHOD)
   public void testProducesInMethodStereotype()
   {
      testProduces(DEPLOY_METHOD);
   }

   @Test
   @OperateOnDeployment(DEPLOY_METHOD)
   public void testConsumesInMethodStereotype()
   {
      testConsumes(DEPLOY_METHOD);
   }

   private void testProduces(String deploymentName)
   {
      WebTarget base = client.target(PortProviderUtil.generateURL("/mediatype", deploymentName));
      Response response = base.path("produces").request().get();

      Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      Assert.assertEquals("Wrong content of the response", "{}", response.readEntity(String.class));
      Assert.assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
   }

   private void testConsumes(String deploymentName)
   {
      WebTarget base = client.target(PortProviderUtil.generateURL("/mediatype", deploymentName));
      Response response = base.path("consumes").request().header("Content-type", MediaType.APPLICATION_XML).post(Entity.xml("<>"));

      Assert.assertEquals(Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(), response.getStatus());
   }
}
