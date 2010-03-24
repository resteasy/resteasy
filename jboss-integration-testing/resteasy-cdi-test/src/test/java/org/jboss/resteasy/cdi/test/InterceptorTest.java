package org.jboss.resteasy.cdi.test;

import org.junit.Test;


public class InterceptorTest extends AbstractResteasyCdiTest
{
   @Test
   public void testInterceptor()
   {
      testPlainTextReadonlyResource(BASE_URI + "interceptor", true);
   }
}
