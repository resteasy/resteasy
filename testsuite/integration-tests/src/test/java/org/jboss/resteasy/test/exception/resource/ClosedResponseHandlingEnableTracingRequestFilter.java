package org.jboss.resteasy.test.exception.resource;

import org.jboss.resteasy.test.exception.ClosedResponseHandlingTest;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;
import org.jboss.resteasy.tracing.api.RESTEasyTracing;
import org.jboss.resteasy.tracing.api.RESTEasyTracingLevel;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching
public class ClosedResponseHandlingEnableTracingRequestFilter implements ContainerRequestFilter {
   @Override
   public void filter(ContainerRequestContext containerRequestContext) throws IOException {
      // force verbose tracing, enabling via finishContainerPrepare()'s contextParams didn't work
      containerRequestContext.setProperty(RESTEasyTracing.PROPERTY_NAME,
            RESTEasyTracingLogger.create(this.toString(), RESTEasyTracingLevel.VERBOSE.name(), ClosedResponseHandlingTest.class.getSimpleName()));
   }
}
