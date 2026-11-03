package org.jboss.resteasy.test.context.resource;

import java.util.concurrent.CompletableFuture;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.core.Response;

/**
 * A client proxy for the {@link TestResource} whose method is invoked with the {@link ExecutorRxInvoker}.
 *
 * @since 8.0
 */
public interface TestResourceProxy {

    @GET
    CompletableFuture<Response> response();
}
