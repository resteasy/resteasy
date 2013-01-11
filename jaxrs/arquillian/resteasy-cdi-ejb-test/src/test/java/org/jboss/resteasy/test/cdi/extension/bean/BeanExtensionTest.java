package org.jboss.resteasy.test.cdi.extension.bean;

import static org.junit.Assert.assertEquals;

import java.util.logging.Logger;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.cdi.extension.bean.Boston;
import org.jboss.resteasy.cdi.extension.bean.BostonBean;
import org.jboss.resteasy.cdi.extension.bean.BostonBeanExtension;
import org.jboss.resteasy.cdi.extension.bean.BostonHolder;
import org.jboss.resteasy.cdi.extension.bean.BostonlLeaf;
import org.jboss.resteasy.cdi.extension.bean.TestReader;
import org.jboss.resteasy.cdi.extension.bean.TestResource;
import org.jboss.resteasy.cdi.injection.JaxRsActivator;
import org.jboss.resteasy.cdi.util.UtilityProducer;
import org.jboss.resteasy.cdi.util.Utilities;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * CDIBeanExtensionTest tests that Resteasy components and beans defined by a CDI extension
 * can be injected into each other.
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 8, 2012
 */
@RunWith(Arquillian.class)
public class BeanExtensionTest
{
   @Inject Logger log;

   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy-extension-test.war")
      .addClasses(JaxRsActivator.class, UtilityProducer.class, Utilities.class)
      .addClasses(BostonBeanExtension.class, Boston.class, BostonBean.class)
      .addClasses(TestResource.class, TestReader.class)
      .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
      .addAsServiceProvider(Extension.class, BostonBeanExtension.class);
      
      JavaArchive jar = ShrinkWrap.create(JavaArchive.class).addClasses(BostonHolder.class, BostonlLeaf.class);
      war.addAsLibrary(jar);
      System.out.println(war.toString(true));
      return war;
   }

   @Test
   public void testBostonBeans() throws Exception
   {
      log.info("starting testBostonBeans()");
      ClientRequest request = new ClientRequest("http://localhost:8080/resteasy-extension-test/rest/extension/boston/");
      ClientResponse<?> response = request.post();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      response.releaseConnection();
   }
}
