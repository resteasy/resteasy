package org.jboss.resteasy.test.core.basic.resource;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import org.junit.Assert;

@Provider
@Priority(10)
public class ContextBeforeEncoderInterceptor implements WriterInterceptor {

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        final String HEADER_ERROR_MESSAGE = "MessageBodyWriterContext in ContextBeforeEncoderInterceptor don't have correct headers";
        Assert.assertFalse(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("after-encoder"));
        Assert.assertFalse(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("encoder"));
        Assert.assertFalse(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("end"));
        context.getHeaders().add("before-encoder", "true");
        context.proceed();
    }
}
