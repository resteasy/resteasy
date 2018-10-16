package org.jboss.resteasy.test.spring.inmodule.resource;

import org.springframework.beans.factory.annotation.Qualifier;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class RequestScopedBeanTestBeanResource {
   @GET
   public String test(@Qualifier("testBean") RequestScopedBeanTestBean bean) {
      return bean.configured;
   }
}
