package org.jboss.resteasy.test.interception.resource;

import javax.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;

@Priority(300)
public class PriorityContainerResponseFilter3 implements ContainerResponseFilter {
   @Override
   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

   }
}
