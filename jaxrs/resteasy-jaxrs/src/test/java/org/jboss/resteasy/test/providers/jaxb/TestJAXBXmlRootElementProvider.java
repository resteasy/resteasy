/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.test.providers.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.jboss.resteasy.plugins.client.httpclient.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * A TestJAXBXmlRootElementProvider.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public class TestJAXBXmlRootElementProvider extends BaseResourceTest
{

   private static final String HTTP_LOCALHOST_8081_JAXB = "http://localhost:8081/jaxb";

   private static final String FAST_PARENT = "Fast Parent";

   private static final String XML_PARENT = "XML Parent";

   private static final Logger logger = LoggerFactory
         .getLogger(TestJAXBXmlRootElementProvider.class);

   private JAXBXmlRootElementClient client;

   private JAXBXmlRootElementFastinfoSetClient fastClient;

   private JAXBElementClient elementClient;

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(JAXBXmlRootElementResource.class);
      ResteasyProviderFactory.initializeInstance();
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      client = ProxyFactory.create(JAXBXmlRootElementClient.class, HTTP_LOCALHOST_8081_JAXB);
      fastClient = ProxyFactory.create(JAXBXmlRootElementFastinfoSetClient.class,
            HTTP_LOCALHOST_8081_JAXB);

      elementClient = ProxyFactory.create(JAXBElementClient.class, HTTP_LOCALHOST_8081_JAXB);
   }

   /**
    * FIXME Comment this
    *
    */
   @Test
   public void testGetParent()
   {
      long start = System.currentTimeMillis();
      Parent parent = client.getParent(XML_PARENT);
      Assert.assertEquals(parent.getName(), XML_PARENT);
      logger.info("completed XML in {}", System.currentTimeMillis() - start);
   }

   @Test
   public void testGetParentElement()
   {
      long start = System.currentTimeMillis();
      JAXBElement<Parent> element = elementClient.getParent(XML_PARENT);
      Parent parent = element.getValue();
      Assert.assertEquals(parent.getName(), XML_PARENT);
      logger.info("completed XML in {}", System.currentTimeMillis() - start);
   }

   //@Test
   public void testGetParentFast()
   {
      long start = System.currentTimeMillis();
      Parent parent = fastClient.getParent(FAST_PARENT);
      Assert.assertNotNull(parent);
      logger.info("completed FastInfoSet in {}", System.currentTimeMillis() - start);
   }

   /**
    * FIXME Comment this
    *
    */
   @Test
   public void testPostParent()
   {
      client.postParent(Parent.createTestParent("TEST"));
   }

   @Test
   public void testPostParentFast()
   {
      fastClient.postParent(Parent.createTestParent("TEST FAST"));
   }

   @Test
   public void testPostParentElement()
   {
      Parent parent = Parent.createTestParent("TEST ELEMENT");
      JAXBElement<Parent> parentElement = new JAXBElement<Parent>(new QName("parent"),
                                                                  Parent.class, parent);
      elementClient.postParent(parentElement);
   }

}
