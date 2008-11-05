package org.jboss.resteasy.springmvc.test.client;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{ "classpath:spring-test-client.xml" })
public class BasicSpringTest
{

   @Autowired
   BasicResource br;

   @Test
   public void testBasic() throws HttpException, IOException
   {
      Assert.assertEquals("/basic/url", br.getURL());

      Assert.assertEquals("test", br.getBasicString());

      Assert.assertEquals("test", br.getBasicObject().getSomething());

      Assert.assertEquals("Hi, I'm custom!", br.getSpringMvcValue());

      Assert.assertEquals(1, br.getSingletonCount().intValue());
      Assert.assertEquals(2, br.getSingletonCount().intValue());

      Assert.assertEquals(1, br.getPrototypeCount().intValue());
      Assert.assertEquals(1, br.getPrototypeCount().intValue());

      Assert.assertEquals("text/plain", br.getContentTypeHeader());
   }
}
