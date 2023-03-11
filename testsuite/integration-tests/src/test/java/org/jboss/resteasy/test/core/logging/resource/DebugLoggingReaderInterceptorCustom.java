package org.jboss.resteasy.test.core.logging.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;

@Provider
public class DebugLoggingReaderInterceptorCustom implements ReaderInterceptor {

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context)
            throws IOException, WebApplicationException {
        InputStream originalInputStream = context.getInputStream();

        String inputString = convertStreamToString(
                originalInputStream);
        inputString += inputString;
        InputStream newStream = new ByteArrayInputStream(
                inputString.getBytes(StandardCharsets.UTF_8));

        context.setInputStream(newStream);

        // proceed
        Object result = context.proceed();
        return result;
    }

    static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is)
                .useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
