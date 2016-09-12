package org.jboss.resteasy.springmvc.test.client;

import org.apache.commons.httpclient.HttpException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{ "classpath:spring-test-client.xml" })
@DirtiesContext
public class BasicSpringTest
{

   @Autowired
   private BasicResource br;

   @Test
   public void testBasic() throws HttpException, IOException
   {
//      ClientResponse<BasicJaxbObject> result = br.getWrongContentTypeBasicObject();
//      Assert.assertEquals(-1, result.getStatus());
      Assert.assertEquals("/basic/url", br.getURL());

      Assert.assertEquals("test", br.getBasicString());
      Assert.assertEquals("something", br.getBasicObject().getSomething());

      Assert.assertEquals("Hi, I'm custom!", br.getSpringMvcValue());

      Assert.assertEquals(1, br.getSingletonCount().intValue());
      Assert.assertEquals(2, br.getSingletonCount().intValue());

      Assert.assertEquals(1, br.getPrototypeCount().intValue());
      Assert.assertEquals(1, br.getPrototypeCount().intValue());

      Assert.assertEquals("text/plain", br.getContentTypeHeader());

      Integer interceptorCount = br
            .getSpringInterceptorCount("afterCompletion");
      
      Assert.assertEquals(new Integer(9), interceptorCount);
      Assert.assertEquals("text/plain", br.getContentTypeHeader());
      Assert.assertEquals("springSomething", br.testSpringXml().getSomething());
//      br.testBogusUrl();
   }
}
