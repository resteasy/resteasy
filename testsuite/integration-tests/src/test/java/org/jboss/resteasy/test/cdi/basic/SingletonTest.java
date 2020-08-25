package org.jboss.resteasy.test.cdi.basic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.ExpectedFailingWithStandaloneMicroprofileConfiguration;
import org.jboss.resteasy.category.NotForBootableJar;
import org.jboss.resteasy.test.cdi.basic.resource.SingletonLocalIF;
import org.jboss.resteasy.test.cdi.basic.resource.SingletonRootResource;
import org.jboss.resteasy.test.cdi.basic.resource.SingletonSubResource;
import org.jboss.resteasy.test.cdi.basic.resource.SingletonTestBean;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for Singleton beans
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category({
    ExpectedFailingWithStandaloneMicroprofileConfiguration.class, // java.lang.NoClassDefFoundError: javax/ejb/EJBException (MP is missing EJB3)
    NotForBootableJar.class // no EJB layer so far
})
public class SingletonTest {
   static Client client;
   protected static final Logger logger = LogManager.getLogger(SingletonTest.class.getName());

   @BeforeClass
   public static void setup() {
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void close() {
      client.close();
   }

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(SingletonTest.class.getSimpleName());
      war.addClasses(SingletonLocalIF.class, SingletonSubResource.class,
            SingletonTestBean.class);
      return TestUtil.finishContainerPrepare(war, null, SingletonRootResource.class);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, SingletonTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Three requests for singleton bean
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testSingleton() throws Exception {
      WebTarget base = client.target(generateURL("/root"));
      String value = base.path("sub").request().get(String.class);
      Assert.assertEquals("Wrong content of response", "hello", value);
      value = base.path("injected").request().get(String.class);
      Assert.assertEquals("Wrong content of response", "true", value);
      value = base.path("intfsub").request().get(String.class);
      logger.info(value);
      Response response = base.path("exception").request().get();
      Assert.assertEquals(HttpResponseCodes.SC_CREATED, response.getStatus());
   }

}
