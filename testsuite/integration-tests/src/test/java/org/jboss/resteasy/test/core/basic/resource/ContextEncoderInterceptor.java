package org.jboss.resteasy.test.core.basic.resource;

import org.jboss.resteasy.annotations.interception.EncoderPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;
import org.junit.Assert;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;

@EncoderPrecedence
@ServerInterceptor
public class ContextEncoderInterceptor implements MessageBodyWriterInterceptor {
    public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException {
        final String HEADER_ERROR_MESSAGE = "MessageBodyWriterContext in ContextEncoderInterceptor don't have correct headers";
        Assert.assertTrue(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("before-encoder"));
        Assert.assertFalse(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("after-encoder"));
        Assert.assertFalse(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("end"));
        context.getHeaders().add("encoder", "true");
        context.proceed();
    }
}
