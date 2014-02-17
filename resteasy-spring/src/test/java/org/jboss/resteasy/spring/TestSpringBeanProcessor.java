package org.jboss.resteasy.spring;

import static org.jboss.resteasy.test.TestPortProvider.createClientRequest;
import junit.framework.Assert;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.spring.SpringBeanProcessor;
import org.jboss.resteasy.spring.beanprocessor.MyInterceptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This class tests a gamut of Spring related functionality including @Configuration
 * beans, @Autowired, scanned beans, interceptors and overall integration
 * between RESTEasy and the Spring ApplicationContext.
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 *
 * @see SpringBeanProcessor
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring-bean-processor-test.xml" })
@DirtiesContext
public class TestSpringBeanProcessor
{

   @Test
   public void testAutoProxy() throws Exception
   {
      checkGet("/intercepted", "customer=bill");
      Assert.assertTrue(MyInterceptor.invoked);
   }

   @Test
   public void testProcessor() throws Exception
   {
      checkGet("", "customer=bill");
   }

   @Test
   public void testPrototyped() throws Exception
   {
      checkGet("/prototyped/1", "bill0");
      checkGet("/prototyped/1", "bill0");
   }

   @Test
   public void testRegistration() throws Exception
   {
      ClientResponse<String> resp = createClientRequest("/registered/singleton/count").post(
            String.class);
      check(resp, 200, "0");
      Assert.assertEquals(404, createClientRequest("/count").post().getStatus());
   }

   @Test
   public void testScanned() throws Exception
   {
      checkGet("/scanned", "Hello");
   }

   @Test
   public void testAutowiredProvider() throws Exception
   {
      checkGet("/customer-name?name=Solomon", "customer=Solomon");
      checkGet("/customer-object?customer=Solomon", "Solomon");
   }

   private static void checkGet(String url, String expectedResponse) throws Exception
   {
      check(createClientRequest(url).get(String.class), 200, expectedResponse);
   }

   private static void check(ClientResponse<String> resp, int expectedStatus,
         String expectedResponse)
   {
      Assert.assertEquals(expectedStatus, resp.getStatus());
      Assert.assertEquals(resp.getEntity(), expectedResponse);
   }

}
