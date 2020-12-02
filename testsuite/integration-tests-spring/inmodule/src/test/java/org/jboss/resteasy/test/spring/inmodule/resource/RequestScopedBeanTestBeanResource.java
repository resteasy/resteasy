package org.jboss.resteasy.test.spring.inmodule.resource;

import org.springframework.beans.factory.annotation.Qualifier;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/")
public class RequestScopedBeanTestBeanResource {
   @GET
   public String test(@Qualifier("testBean") RequestScopedBeanTestBean bean) {
      return bean.configured;
   }
}
