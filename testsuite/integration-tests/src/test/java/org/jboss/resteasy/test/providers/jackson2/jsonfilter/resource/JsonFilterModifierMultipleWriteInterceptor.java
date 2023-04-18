package org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import com.fasterxml.jackson.jaxrs.cfg.ObjectWriterInjector;

public class JsonFilterModifierMultipleWriteInterceptor implements WriterInterceptor {
    private ObjectFilterModifierMultiple modifier = new ObjectFilterModifierMultiple();

    @Override
    public void aroundWriteTo(WriterInterceptorContext context)
            throws IOException, WebApplicationException {
        //set a threadlocal modifier
        ObjectWriterInjector.set(modifier);
        context.proceed();
    }
}
