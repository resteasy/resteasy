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

package org.jboss.resteasy.test.providers.sse.resource;

import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Path("/callback")
@RequestScoped
public class SseCallbackResource {
    private static final Logger LOGGER = Logger.getLogger(SseCallbackResource.class);

    @Resource
    private ManagedExecutorService executor;

    @Inject
    private Sse sse;

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @Path("send/{count}")
    public void sendEvents(@Context final SseEventSink eventSink, @PathParam("count") final int count) {
        LOGGER.debugf("SseCallbackResource.sendEvents(%d)", count);
        executor.execute(() -> {
            try (SseEventSink sink = eventSink) {
                for (int i = 0; i < count; i++) {
                    sink.send(sse.newEvent("event" + i));
                }
            }
        });
    }
}
