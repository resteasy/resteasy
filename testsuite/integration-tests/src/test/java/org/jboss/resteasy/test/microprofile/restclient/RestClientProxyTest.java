package org.jboss.resteasy.test.microprofile.restclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URL;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.category.ExpectedFailingOnWildFly12;
import org.jboss.resteasy.category.ExpectedFailingOnWildFly13;
import org.jboss.resteasy.client.microprofile.MicroprofileClientBuilderResolver;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class RestClientProxyTest
{

   @ArquillianResource
   URL url;

   @Deployment
   public static Archive<?> deploy()
   {
      WebArchive war = TestUtil.prepareArchive(RestClientProxyTest.class.getSimpleName());
      war.addClass(RestClientProxyTest.class);
      war.addPackage(HelloResource.class.getPackage());
      war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      war.addClass(PortProviderUtil.class);
      war.addClass(ExpectedFailingOnWildFly12.class);
      war.addClass(ExpectedFailingOnWildFly13.class);
      war.addClass(Category.class);
      war.addAsManifestResource(new StringAsset("Dependencies: org.eclipse.microprofile.restclient\n"), "MANIFEST.MF");
      return TestUtil.finishContainerPrepare(war, null);
   }

   private String generateURL(String path)
   {
      return PortProviderUtil.generateURL(path, RestClientProxyTest.class.getSimpleName());
   }

   @Test
   @Category({ExpectedFailingOnWildFly12.class, ExpectedFailingOnWildFly13.class})
   public void testGetClient() throws Exception
   {
      RestClientBuilder builder = RestClientBuilder.newBuilder();
      RestClientBuilder resteasyBuilder = MicroprofileClientBuilderResolver.instance().newBuilder();
      assertEquals(resteasyBuilder.getClass(), builder.getClass());
      HelloClient client = builder.baseUrl(new URL(generateURL(""))).build(HelloClient.class);

      assertNotNull(client);
      assertEquals("Hello", client.hello());
   }

}