package org.jboss.resteasy.test.core.logging.resource;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.io.OutputStream;

@Provider
public class DebugLoggingWriterInterceptorCustom implements WriterInterceptor {

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        OutputStream outputStream = context.getOutputStream();
        String responseContent = "wi_"; // wi = writer interceptor
        outputStream.write(responseContent.getBytes());
        context.setOutputStream(outputStream);
        context.proceed();
    }
}

