package org.jboss.resteasy.test.cdi.interceptors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.ExpectedFailingWithStandaloneMicroprofileConfiguration;
import org.jboss.resteasy.test.cdi.interceptors.resource.TimerInterceptorResource;
import org.jboss.resteasy.test.cdi.interceptors.resource.TimerInterceptorResourceIntf;
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

import static org.junit.Assert.assertEquals;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for interceptors with timer service.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category({
    ExpectedFailingWithStandaloneMicroprofileConfiguration.class // java.lang.NoClassDefFoundError: javax/ejb/Timer (MP is missing EJB3)
})
public class TimerInterceptorTest {
   protected static final Logger log = LogManager.getLogger(TimerInterceptorTest.class.getName());

   @Deployment(testable = false)
   public static Archive<?> createTestArchive() {
      WebArchive war = TestUtil.prepareArchive(TimerInterceptorTest.class.getSimpleName())
            .addClasses(UtilityProducer.class, PortProviderUtil.class)
            .addClasses(TimerInterceptorResourceIntf.class, TimerInterceptorResource.class)
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      return war;
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, TimerInterceptorTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Timer is sheduled and than is called.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testTimerInterceptor() throws Exception {
      Client client = ClientBuilder.newClient();

      // Schedule timer.
      WebTarget base = client.target(generateURL("/timer/schedule"));
      Response response = base.request().get();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      response.close();

      // Verify timer expired and timer interceptor was executed.
      base = client.target(generateURL("/timer/test"));
      response = base.request().get();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      response.close();

      client.close();
   }
}
