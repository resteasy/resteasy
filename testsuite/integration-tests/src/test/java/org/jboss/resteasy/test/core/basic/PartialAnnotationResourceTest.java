package org.jboss.resteasy.test.core.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.resteasy.test.core.basic.resource.PartialAnnotationResource;
import org.jboss.resteasy.test.core.basic.resource.PartialAnnotationResourceImpl;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Configuration
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for RESTEASY-798.
 * @tpSince RESTEasy 3.5.1
 */
@RunWith(Arquillian.class)
@RunAsClient
public class PartialAnnotationResourceTest
{
   static ResteasyClient client;

   @Deployment
   public static Archive<?> deploySimpleResource()
   {
      WebArchive war = TestUtil.prepareArchive(PartialAnnotationResourceTest.class.getSimpleName());
      war.addClasses(PartialAnnotationResource.class, PartialAnnotationResourceImpl.class);
      return TestUtil.finishContainerPrepare(war, null, PartialAnnotationResourceImpl.class);
   }

   @Before
   public void init()
   {
      client = (ResteasyClient)ClientBuilder.newClient();
   }

   @After
   public void after() throws Exception
   {
      client.close();
   }

   /**
    * @tpTestDetails Test basic functionality of test resource
    * @tpSince RESTEasy 3.5.1
    */
   @Test
   public void test()
   {
      PartialAnnotationResource proxy = client
            .target(PortProviderUtil.generateBaseUrl(PartialAnnotationResourceTest.class.getSimpleName()))
            .proxy(PartialAnnotationResource.class);
      Assert.assertEquals(PartialAnnotationResourceImpl.BAR_RESPONSE, proxy.bar());
   }
}
