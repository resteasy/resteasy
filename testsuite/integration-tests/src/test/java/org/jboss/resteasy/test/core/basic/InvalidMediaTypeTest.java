package org.jboss.resteasy.test.core.basic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.resteasy.test.core.basic.resource.InvalidMediaTypeResource;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Regression for RESTEASY-699
 */
@RunWith(Arquillian.class)
@RunAsClient
public class InvalidMediaTypeTest {

   protected static final Logger logger = LogManager.getLogger(InvalidMediaTypeTest.class.getName());

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(InvalidMediaTypeTest.class.getSimpleName());
      return TestUtil.finishContainerPrepare(war, null, InvalidMediaTypeResource.class);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, InvalidMediaTypeTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Check various wrong media type
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testInvalidMediaTypes() throws Exception {
      ResteasyClient client = (ResteasyClient)ClientBuilder.newClient();
      Invocation.Builder request = client.target(generateURL("/test")).request();

      // Missing type or subtype
      doTest(request, "/");
      doTest(request, "/*");
      doTest(request, "*/");
      doTest(request, "text/");
      doTest(request, "/plain");

      // Illegal white space
      doTest(request, " /*");
      doTest(request, "/* ");
      doTest(request, " /* ");
      doTest(request, "/ *");
      doTest(request, "* /");
      doTest(request, " / *");
      doTest(request, "* / ");
      doTest(request, "* / *");
      doTest(request, " * / *");
      doTest(request, "* / * ");
      doTest(request, "text/ plain");
      doTest(request, "text /plain");
      doTest(request, " text/plain");
      doTest(request, "text/plain ");
      doTest(request, " text/plain ");
      doTest(request, " text / plain ");
      client.close();
   }

   private void doTest(Invocation.Builder request, String mediaType) {
      request.accept(mediaType);
      Response response = request.get();
      logger.info("mediaType: " + mediaType + "");
      logger.info("status: " + response.getStatus());
      Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
      response.close();
   }
}
