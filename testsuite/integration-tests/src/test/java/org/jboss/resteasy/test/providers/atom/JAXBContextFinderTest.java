package org.jboss.resteasy.test.providers.atom;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.test.providers.atom.resource.JAXBContextFinderAtomServer;
import org.jboss.resteasy.test.providers.atom.resource.JAXBContextFinderCustomerAtom;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.ReasteasyTestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.Iterator;

/**
 * @tpSubChapter Atom provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test integration of atom provider and JAXB Context finder
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JAXBContextFinderTest {

   static ResteasyClient client;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = ReasteasyTestUtil.prepareArchive(JAXBContextFinderTest.class.getSimpleName());
      war.addClass(JAXBContextFinderCustomerAtom.class);
      return ReasteasyTestUtil.finishContainerPrepare(war, null, JAXBContextFinderAtomServer.class);
   }

   @Before
   public void init() {
      client = (ResteasyClient)ClientBuilder.newClient();
   }

   @After
   public void after() throws Exception {
      client.close();
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, JAXBContextFinderTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Test new client
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testAtomFeedNewClient() throws Exception {
      Response response = client.target(generateURL("/atom/feed")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Feed feed = response.readEntity(Feed.class);
      Iterator<Entry> it = feed.getEntries().iterator();
      Entry entry1 = it.next();
      Entry entry2 = it.next();
      Field field = Entry.class.getDeclaredField("finder");
      field.setAccessible(true);
      Assert.assertNotNull("First feet is not correct", field.get(entry1));
      Assert.assertEquals("Second feet is not correct", field.get(entry1), field.get(entry2));
      response.close();
   }
}
