package org.jboss.resteasy.test.core.basic.resource;

import org.jboss.resteasy.annotations.interception.Precedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;
import org.junit.Assert;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;

@Precedence("AFTER_ENCODER")
@ServerInterceptor
public class ContextAfterEncoderInterceptor implements MessageBodyWriterInterceptor {
    public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException {
        final String HEADER_ERROR_MESSAGE = "MessageBodyWriterContext in ContextAfterEncoderInterceptor don't have correct headers";
        Assert.assertTrue(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("before-encoder"));
        Assert.assertTrue(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("encoder"));
        Assert.assertFalse(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("end"));
        context.getHeaders().add("after-encoder", "true");
        context.proceed();
    }
}