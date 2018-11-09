package org.jboss.resteasy.test.exception.resource;

import org.jboss.resteasy.test.exception.ClosedResponseHandlingTest;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;
import org.jboss.resteasy.tracing.api.RESTEasyTracing;
import org.jboss.resteasy.tracing.api.RESTEasyTracingLevel;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching
public class ClosedResponseHandlingEnableTracingRequestFilter implements ContainerRequestFilter {
   @Override
   public void filter(ContainerRequestContext containerRequestContext) throws IOException {
      // force verbose tracing, enabling via finishContainerPrepare()'s contextParams didn't work
      containerRequestContext.setProperty(RESTEasyTracing.PROPERTY_NAME,
            RESTEasyTracingLogger.create(RESTEasyTracingLevel.VERBOSE.name(), ClosedResponseHandlingTest.class.getSimpleName()));
   }
}
