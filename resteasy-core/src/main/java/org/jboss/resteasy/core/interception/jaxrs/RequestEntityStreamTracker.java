/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2024 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.resteasy.core.interception.jaxrs;

import java.io.IOException;
import java.io.InputStream;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpRequest;

public final class RequestEntityStreamTracker {
    private static final Logger LOG = Logger.getLogger(RequestEntityStreamTracker.class);
    private static final String ATTRIBUTE = RequestEntityStreamTracker.class.getName();

    private final InputStream originalEntityStream;
    private InputStream replacementEntityStream;
    private boolean closed;

    private RequestEntityStreamTracker(InputStream originalEntityStream) {
        this.originalEntityStream = originalEntityStream;
    }

    public static RequestEntityStreamTracker getOrCreate(HttpRequest request) {
        synchronized (request) {
            RequestEntityStreamTracker tracker = get(request);
            if (tracker == null) {
                tracker = new RequestEntityStreamTracker(request.getInputStream());
                request.setAttribute(ATTRIBUTE, tracker);
            }
            return tracker;
        }
    }

    public static RequestEntityStreamTracker get(HttpRequest request) {
        return (RequestEntityStreamTracker) request.getAttribute(ATTRIBUTE);
    }

    public synchronized void setReplacement(InputStream entityStream) {
        replacementEntityStream = entityStream;
        closed = false;
    }

    public void closeReplacement() {
        final InputStream entityStream;
        synchronized (this) {
            if (closed || replacementEntityStream == null || replacementEntityStream == originalEntityStream) {
                return;
            }
            closed = true;
            entityStream = replacementEntityStream;
            replacementEntityStream = originalEntityStream;
        }

        try {
            entityStream.close();
        } catch (IOException e) {
            LOG.warn("Failed to close the replacement request entity stream", e);
        }
    }
}
