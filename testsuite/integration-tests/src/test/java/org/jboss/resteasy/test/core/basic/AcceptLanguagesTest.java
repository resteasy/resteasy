package org.jboss.resteasy.test.core.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.core.basic.resource.AcceptLanguagesResource;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Localization
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class AcceptLanguagesTest {

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(AcceptLanguagesTest.class.getSimpleName());
      return TestUtil.finishContainerPrepare(war, null, AcceptLanguagesResource.class);
   }

   /**
    * @tpTestDetails Check some languages for accepting
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testLanguages() throws Exception {
      Client client = ClientBuilder.newClient();
      WebTarget base = client.target(PortProviderUtil.generateURL("/lang", AcceptLanguagesTest.class.getSimpleName()));
      Response response = base.request().header("Accept-Language", "en-US;q=0,en;q=0.8,de-AT,de;q=0.9").get();

      Assert.assertEquals(response.getStatus(), HttpResponseCodes.SC_OK);

      response.close();
      client.close();
   }

}
