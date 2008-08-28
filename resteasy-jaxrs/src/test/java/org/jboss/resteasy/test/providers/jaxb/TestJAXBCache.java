/*
 * JBoss, the OpenSource J2EE webOS Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jboss.resteasy.test.providers.jaxb;

import javax.xml.bind.JAXBContext;

import org.jboss.resteasy.core.ExceptionAdapter;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBCache;
import org.jboss.resteasy.test.providers.jaxb.data.Order;
import org.jboss.resteasy.test.providers.jaxb.generated.order.Itemtype;
import org.jboss.resteasy.test.providers.jaxb.generated.order.Ordertype;
import org.junit.Assert;
import org.junit.Test;

/**
 * A TestJAXBCache.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public class TestJAXBCache
{

   /**
    * Test method for
    * {@link org.jboss.resteasy.plugins.providers.jaxb.JAXBCache#getJAXBContext(java.lang.Class)}.
    */
   @Test
   public void testGetJAXBContextForXmlTypeAnnotatedClass()
   {
      JAXBContext ctx = JAXBCache.instance().getJAXBContext(Ordertype.class);
      Assert.assertTrue(ctx != null);
   }

   @Test
   public void testGetJAXBContextForXmlRootEntityAnnotatedClass()
   {
      JAXBContext ctx = JAXBCache.instance().getJAXBContext(Order.class);
      Assert.assertTrue(ctx != null);
   }

   /**
    * Test method for {@link
    * org.jboss.resteasy.plugins.providers.jaxb.JAXBCache#getJAXBContext(java.lang.Class<?>[])}.
    */
   @Test
   public void testGetJAXBContextClassArray()
   {
      JAXBContext ctx = JAXBCache.instance().getJAXBContext(Ordertype.class, Itemtype.class);
      Assert.assertTrue(ctx != null);
   }

   /**
    * Test method for
    * {@link org.jboss.resteasy.plugins.providers.jaxb.JAXBCache#getJAXBContext(java.lang.String[])}.
    */
   @Test
   public void testGetJAXBContextStringArray()
   {
      String[] packageNames = {"org.jboss.resteasy.test.providers.jaxb.generated.order"};
      JAXBContext ctx = JAXBCache.instance().getJAXBContext(packageNames);
      Assert.assertTrue(ctx != null);
   }

   /**
    * Test method for
    * {@link org.jboss.resteasy.plugins.providers.jaxb.JAXBCache#getJAXBContext(java.lang.String)}.
    */
   @Test
   public void testGetJAXBContextString()
   {
      String packageName = "org.jboss.resteasy.test.providers.jaxb.generated.order";
      JAXBContext ctx = JAXBCache.instance().getJAXBContext(packageName);
      Assert.assertTrue(ctx != null);
   }

   @Test(expected = ExceptionAdapter.class)
   public void testGetJAXBContextStringFail()
   {
      String packageName = "org.jboss.resteasy.test.providers.jaxb.data";
      JAXBContext ctx = JAXBCache.instance().getJAXBContext(packageName);
      Assert.assertTrue(ctx != null);
   }

}
