package org.jboss.resteasy.test.core.basic.resource;

import org.jboss.resteasy.annotations.interception.Precedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;
import org.junit.Assert;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;

@Precedence("BEFORE_ENCODER")
@ServerInterceptor
public class ContextBeforeEncoderInterceptor implements MessageBodyWriterInterceptor {
    public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException {
        final String HEADER_ERROR_MESSAGE = "MessageBodyWriterContext in ContextBeforeEncoderInterceptor don't have correct headers";
        Assert.assertFalse(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("after-encoder"));
        Assert.assertFalse(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("encoder"));
        Assert.assertFalse(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("end"));
        context.getHeaders().add("before-encoder", "true");
        context.proceed();
    }
}
