package org.jboss.resteasy.test.asynch.resource;

import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;

@Path("/")
public class AsyncInjectionResource2 {

    @Context
    private AsyncInjectionContext resolvedContextField;
    @Context
    private CompletionStage<AsyncInjectionContext> asyncContextField;

    @Context
    @AsyncInjectionContextAsyncSpecifier
    private AsyncInjectionContext resolvedContextFieldAsync;

    public AsyncInjectionResource2() {
    }

    public AsyncInjectionContext resolvedContextField() {
        return resolvedContextField;
    }

    public CompletionStage<AsyncInjectionContext> asyncContextField() {
        return asyncContextField;
    }

    public AsyncInjectionContext resolvedContextFieldAsync() {
        return resolvedContextFieldAsync;
    }
}
