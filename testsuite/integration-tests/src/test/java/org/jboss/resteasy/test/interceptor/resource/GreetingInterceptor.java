package org.jboss.resteasy.test.interceptor.resource;

import java.io.IOException;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

public class GreetingInterceptor implements WriterInterceptor {

    @Override
    public void aroundWriteTo(WriterInterceptorContext context)
            throws IOException, WebApplicationException {
        String entity = (String) context.getEntity();
        if (entity != null) {
            context.setEntity("Hello " + entity + " !");
        }
        context.proceed();
    }

}
