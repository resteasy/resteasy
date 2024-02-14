package org.jboss.resteasy.test.core.basic.resource;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import org.junit.jupiter.api.Assertions;

@Provider
@Priority(30)
public class ContextAfterEncoderInterceptor implements WriterInterceptor {

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        final String HEADER_ERROR_MESSAGE = "MessageBodyWriterContext in ContextAfterEncoderInterceptor don't have correct headers";
        Assertions.assertTrue(context.getHeaders().containsKey("before-encoder"), HEADER_ERROR_MESSAGE);
        Assertions.assertTrue(context.getHeaders().containsKey("encoder"), HEADER_ERROR_MESSAGE);
        Assertions.assertFalse(context.getHeaders().containsKey("end"), HEADER_ERROR_MESSAGE);
        context.getHeaders().add("after-encoder", "true");
        context.proceed();
    }
}
