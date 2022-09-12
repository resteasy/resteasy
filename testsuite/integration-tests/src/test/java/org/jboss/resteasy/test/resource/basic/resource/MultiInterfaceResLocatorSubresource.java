package org.jboss.resteasy.test.resource.basic.resource;

import jakarta.ws.rs.Path;

@Path("")
public class MultiInterfaceResLocatorSubresource implements MultiInterfaceResLocatorIntf1, MultiInterfaceResLocatorIntf2 {
   @Override
   public String resourceMethod1() {
      return "resourceMethod1";
   }

   @Override
   public String resourceMethod2() {
      return "resourceMethod2";
   }
}
