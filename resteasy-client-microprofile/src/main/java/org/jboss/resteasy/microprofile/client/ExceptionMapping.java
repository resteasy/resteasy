/**
 * Copyright 2015-2017 Red Hat, Inc, and individual contributors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.resteasy.microprofile.client;

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
public class ExceptionMapping implements ClientResponseFilter {
    public static class HandlerException extends ResponseProcessingException {
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
                    if (exc.isAssignableFrom(exception.getClass())) throw (Exception) exception;
                }
            }
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
