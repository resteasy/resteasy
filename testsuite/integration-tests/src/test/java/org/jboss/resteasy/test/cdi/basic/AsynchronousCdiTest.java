package org.jboss.resteasy.test.cdi.basic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.ExpectedFailingWithStandaloneMicroprofileConfiguration;
import org.jboss.resteasy.category.NotForBootableJar;
import org.jboss.resteasy.test.cdi.basic.resource.AsynchronousResource;
import org.jboss.resteasy.test.cdi.basic.resource.AsynchronousStateless;
import org.jboss.resteasy.test.cdi.basic.resource.AsynchronousStatelessLocal;
import org.jboss.resteasy.test.cdi.util.UtilityProducer;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for asynchronous behavior of RESTEasy with CDI.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category(NotForBootableJar.class) // no EJB layer so far
public class AsynchronousCdiTest {

   public static final Long DELAY = 5000L;

   protected static final Logger log = LogManager.getLogger(AsynchronousCdiTest.class.getName());

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, AsynchronousCdiTest.class.getSimpleName());
   }

   @Deployment
   public static Archive<?> createTestArchive() {
      WebArchive war = TestUtil.prepareArchive(AsynchronousCdiTest.class.getSimpleName());
      war.addClasses(UtilityProducer.class)
            .addClasses(AsynchronousStatelessLocal.class, AsynchronousStateless.class)
            .addClasses(AsynchronousResource.class, AsynchronousCdiTest.class)
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      return war;
   }

   /**
    * @tpTestDetails Delay is in stateless bean.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   @Category({
       ExpectedFailingWithStandaloneMicroprofileConfiguration.class}   //  java.lang.NoClassDefFoundError: javax/ejb/AsyncResult (MP is missing EJB3)
   )
   public void testAsynchJaxRs() throws Exception {
      Client client = ClientBuilder.newClient();
      WebTarget base = client.target(generateURL("/asynch/simple"));

      long start = System.currentTimeMillis();
      Response response = base.request().get();

      assertThat("Response was sent before delay elapsed", System.currentTimeMillis() - start, is(greaterThan(DELAY)));
      assertEquals(200, response.getStatus());
      client.close();
   }

   /**
    * @tpTestDetails Delay is in RESTEasy resource.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testAsynchResourceAsynchEJB() throws Exception {
      Client client = ClientBuilder.newClient();
      WebTarget base = client.target(generateURL("/asynch/ejb"));

      long start = System.currentTimeMillis();
      Response response = base.request().get();

      assertThat("Response was sent before delay elapsed", System.currentTimeMillis() - start, is(greaterThan(DELAY)));
      assertEquals(200, response.getStatus());
      client.close();
   }
}
