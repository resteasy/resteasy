package org.jboss.resteasy.microprofile.client;

import org.jboss.logging.Logger;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponseContextImpl;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This implementation is a bit of a hack and dependent on Resteasy internals.
 * We throw a ResponseProcessingExceptoin that hides the Response object
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ExceptionMapping implements ClientResponseFilter {
    public static class HandlerException extends ResponseProcessingException {
        private static final Logger LOGGER = Logger.getLogger(ExceptionMapping.HandlerException.class);
        protected ClientResponse handled;
        protected List<ResponseExceptionMapper> candidates;

        public HandlerException(final ClientResponseContext context, final List<ResponseExceptionMapper> candidates) {
            super(null, "Handled Internally");
            this.handled = ((ClientResponseContextImpl) context).getClientResponse();
            this.candidates = candidates;
        }

        public void mapException(final Method method) throws Exception {
            // we cannot close the Response as a pointer to the Response could be used in the application
            // So, instead, let's buffer it which will close the underlying stream.
            handled.bufferEntity();
            for (ResponseExceptionMapper mapper : candidates) {
                Throwable exception = mapper.toThrowable(handled);
                if (exception instanceof RuntimeException) throw (RuntimeException) exception;
                if (exception instanceof Error) throw (Error) exception;
                for (Class exc : method.getExceptionTypes()) {
                    if (exception != null && exc.isAssignableFrom(exception.getClass())) {
                        throw (Exception) exception;
                    }
                }
            }
            // falling through to here means no applicable exception mapper found
            // or applicable mapper returned null
            LOGGER.warnf("No default ResponseExceptionMapper found or user's ResponseExceptionMapper returned null."
                + "  Response status: %s  messge: %s", handled.getStatus(), handled.getReasonPhrase());

        }
    }

    public ExceptionMapping(final Set<Object> instances) {
        this.instances = instances;
    }

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {

        Response response = new PartialResponse(responseContext);

        List<ResponseExceptionMapper> candidates = new LinkedList<>();
        for (Object o : instances) {
            if (o instanceof ResponseExceptionMapper) {
                ResponseExceptionMapper candidate = (ResponseExceptionMapper) o;
                if (candidate.handles(response.getStatus(), response.getHeaders())) {
                    candidates.add(candidate);
                }
            }
        }
        if (candidates.isEmpty()) return;

        candidates.sort(
                (m1, m2) -> Integer.compare(m1.getPriority(), m2.getPriority())
        );
        throw new HandlerException(responseContext, candidates);
    }

    private Set<Object> instances;
}
