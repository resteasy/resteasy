package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

public class ProxyJaxbResource implements ProxyJaxbResourceIntf {
   public Response getCredits(@PathParam("userId") String userId) {
      ProxyJaxbCredit credit = new ProxyJaxbCredit();
      credit.setName("foobar");
      return Response.ok(credit).build();
   }
}
