package org.jboss.resteasy.cdi.test;

import org.junit.Test;


public class AlternativeTest extends AbstractResteasyCdiTest
{
   @Test
   public void testAlternative()
   {
      testPlainTextReadonlyResource(BASE_URI + "alternative", "MockResource");
   }
}
