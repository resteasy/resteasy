package org.jboss.resteasy.test.asyncio;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

@Provider
public class MyTypeInterceptor implements WriterInterceptor {

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        if ("Hello".equals(context.getEntity())) {
            context.setEntity(new MyType());
            context.setMediaType(MediaType.TEXT_PLAIN_TYPE);
            context.setType(MyType.class);
        }
        context.proceed();
    }

}
