package org.jboss.resteasy.cdi.test.intf;

import org.jboss.resteasy.cdi.Utils;
import org.junit.Test;

import static org.junit.Assert.*;

public class JaxrsComponentDetectionTest
{
   @Test
   public void testRootResource()
   {
      assertTrue(Utils.isJaxrsResource(RootResource.class));
      assertTrue(Utils.isJaxrsComponent(RootResource.class));
   }
   
   @Test
   public void testSubresource()
   {
      assertTrue(Utils.isJaxrsResource(Subresource.class));
      assertTrue(Utils.isJaxrsComponent(Subresource.class));
   }
   
   @Test
   public void testApplicationSubclass()
   {
      assertTrue(Utils.isJaxrsComponent(Subresource.class));
   }
   
   @Test
   public void testProvider()
   {
      assertTrue(Utils.isJaxrsComponent(SampleProvider.class));
   }
}
