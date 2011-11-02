/*
 * JBoss, the OpenSource J2EE webOS Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jboss.resteasy.test.providers.jaxb;

import junit.framework.Assert;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.plugins.providers.ProviderHelper;
import org.jboss.resteasy.test.BaseResourceTest;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.jboss.resteasy.test.providers.jaxb.data.Order;
import org.jboss.resteasy.test.providers.jaxb.generated.order.Ordertype;
import org.junit.Before;
import org.junit.Test;

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
   @SuppressWarnings("unused")
   private static final Logger logger = Logger.getLogger(TestXmlJAXBProviders.class);

   private static final String URL = generateURL("/jaxb/orders");

   private XmlOrderClient client;

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(OrderResource.class);
      //RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
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

   /**
    * This test was commented out in RESTEasy 2.2.
    * 
    * The file order_123.xml is an Order, not an Ordertype.
    */
//   @Test
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

   /**
    * This test was commented out in RESTEasy 2.2.
    * 
    * It was fixed by assigning the result of client.getOrderById() to an
    * Order instead of an Ordertype.
    */
   @Test
   public void testGetOrder()
   {
      Order order = client.getOrderById("order_123");
      Assert.assertEquals("Ryan J. McDonough", order.getPerson());
   }

   
   /**
    * This test is the RESTEasy client framework version of the original
    * testGetOrderWithParams().
    */
   @Test
   public void testGetOrderWithParams() throws Exception
   {
      ClientRequest request = new ClientRequest(URL + "/order_123");
      request.getHeaders().add(JAXBHelper.FORMAT_XML_HEADER, "true");
      ClientResponse<InputStream> response = request.get(InputStream.class);
      Assert.assertEquals(200, response.getStatus());
      ProviderHelper.writeTo(response.getEntity(), System.out);
      response.releaseConnection();
   }
   
   /**
    * This test is new.
    * 
    * It is the RESTEasy client framework version of the original
    * testGetOrderWithParams(), except that it unmarshals the returned
    * order from an OutputStream and tests its value, instead of just
    * printing it out.
    */
   @Test
   public void testGetOrderAndUnmarshal() throws Exception
   {
      ClientRequest request = new ClientRequest(URL + "/order_123");
      request.header(JAXBHelper.FORMAT_XML_HEADER, "true");
      ClientResponse<InputStream> response = request.get(InputStream.class);
      Assert.assertEquals(200, response.getStatus());
      JAXBContext jaxb = JAXBContext.newInstance(Order.class);
      Unmarshaller u = jaxb.createUnmarshaller();
      Order order = (Order) u.unmarshal(response.getEntity());
      Assert.assertNotNull(order);
      Assert.assertEquals("Ryan J. McDonough", order.getPerson());
      response.releaseConnection();
   }

   /**
    * This test is new.
    * 
    * It is the RESTEasy client framework version of the original
    * testGetOrderWithParams(), except that it uses the client framework
    * to implicitly unmarshal the returned order and it tests its value,
    * instead of just printing it out.
    */
   @Test
   public void testGetOrderWithParamsToOrder() throws Exception
   {
      ClientRequest request = new ClientRequest(URL + "/order_123");
      request.getHeaders().add(JAXBHelper.FORMAT_XML_HEADER, "true");
      ClientResponse<Order> response = request.get(Order.class);
      Assert.assertEquals(200, response.getStatus());
      Order order = response.getEntity();
      Assert.assertEquals("Ryan J. McDonough", order.getPerson());
   }
   
   /**
    * This test was commented out in RESTEasy 2.2.
    * 
    * It was fixed by assigning the result of JAXBHelper.unmarshall(() to an
    * Order instead of an Ordertype.  Also, an assert had to commented in
    * OrderResource.updateOrder().
    */
   @Test
   public void testUpdateOrder()
   {
      InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(
              "orders/order_123.xml");
      Order order = JAXBHelper.unmarshall(Order.class, in).getValue();
      int initialItemCount = order.getItems().size();
      order = client.updateOrder(order, "order_123");
      Assert.assertEquals("Ryan J. McDonough", order.getPerson());
      Assert.assertNotSame(initialItemCount, order.getItems().size());
      Assert.assertEquals(3, order.getItems().size());
   }

}
