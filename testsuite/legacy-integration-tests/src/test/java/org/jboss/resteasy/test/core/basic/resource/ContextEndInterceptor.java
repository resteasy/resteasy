package org.jboss.resteasy.test.core.basic.resource;

import org.jboss.resteasy.annotations.interception.Precedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;
import org.junit.Assert;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;

@Precedence("END")
@ServerInterceptor
public class ContextEndInterceptor implements MessageBodyWriterInterceptor {
    public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException {
        final String HEADER_ERROR_MESSAGE = "MessageBodyWriterContext in ContextEndInterceptor don't have correct headers";
        Assert.assertTrue(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("before-encoder"));
        Assert.assertTrue(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("after-encoder"));
        Assert.assertTrue(HEADER_ERROR_MESSAGE, context.getHeaders().containsKey("encoder"));
        context.getHeaders().add("end", "true");
        context.proceed();
    }
}