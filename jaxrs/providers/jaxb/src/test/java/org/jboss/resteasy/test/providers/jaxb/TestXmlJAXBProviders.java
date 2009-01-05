/*
 * JBoss, the OpenSource J2EE webOS Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jboss.resteasy.test.providers.jaxb;

import junit.framework.Assert;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.ProviderHelper;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBHelper;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.jboss.resteasy.test.providers.jaxb.data.Order;
import org.jboss.resteasy.test.providers.jaxb.generated.order.Ordertype;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * A TestXmlJAXBProviders.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public class TestXmlJAXBProviders extends BaseResourceTest
{
   private static final Logger logger = LoggerFactory.getLogger(TestXmlJAXBProviders.class);

   private static final String URL = generateURL("/jaxb/orders");

   private XmlOrderClient client;

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(OrderResource.class);
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      client = ProxyFactory.create(XmlOrderClient.class, URL);
   }

   @Test
   public void testUnmarshalOrder() throws Exception
   {
      InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(
              "orders/order_123.xml");
      Order order = JAXBHelper.unmarshall(Order.class, in).getValue();

      Assert.assertNotNull(order);
      Assert.assertEquals("Ryan J. McDonough", order.getPerson());
   }

   // @Test
   public void testUnmarshalOrdertype() throws Exception
   {
      InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(
              "order_123.xml");
      JAXBContext jaxb = JAXBContext.newInstance(Ordertype.class);
      Unmarshaller u = jaxb.createUnmarshaller();
      Ordertype order = (Ordertype) u.unmarshal(in);
      Assert.assertNotNull(order);
      Assert.assertEquals("Ryan J. McDonough", order.getPerson());
   }

   // @Test
   public void testGetOrder()
   {
      Ordertype order = client.getOrderById("order_123");
      Assert.assertEquals("Ryan J. McDonough", order.getPerson());
   }

   @Test
   public void testGetOrderWithParams() throws Exception
   {
      HttpClient httpClient = new HttpClient();
      GetMethod method = new GetMethod(URL + "/order_123");
      method.addRequestHeader(JAXBHelper.FORMAT_XML_HEADER, "true");
      int status = httpClient.executeMethod(method);
      Assert.assertEquals(200, status);
      ProviderHelper.writeTo(method.getResponseBodyAsStream(), System.out);
   }

   // @Test
   public void testUpdateOrder()
   {
      InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(
              "orders/order_123.xml");
      Ordertype order = JAXBHelper.unmarshall(Ordertype.class, in).getValue();
      int initialItemCount = order.getItem().size();
      order = client.updateOrder(order, "order_123");
      Assert.assertEquals("Ryan J. McDonough", order.getPerson());
      Assert.assertNotSame(initialItemCount, order.getItem().size());
      Assert.assertEquals(3, order.getItem().size());
   }

}
