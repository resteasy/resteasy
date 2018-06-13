package org.jboss.resteasy.test.asynch.resource;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;

@Provider
@ServerInterceptor
public class AsyncPostProcessingMsgBodyWriterInterceptor implements MessageBodyWriterInterceptor {
    public static volatile boolean called;

    public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException {
        called = true;
        context.proceed();
    }
}
