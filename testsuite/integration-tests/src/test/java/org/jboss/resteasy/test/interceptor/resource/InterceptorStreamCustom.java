package org.jboss.resteasy.test.interceptor.resource;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Provider
public class InterceptorStreamCustom implements ReaderInterceptor, WriterInterceptor {

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

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        OutputStream outputStream = context.getOutputStream();
        String responseContent = "writer_interceptor_";
        outputStream.write(responseContent.getBytes());
        context.setOutputStream(outputStream);
        context.proceed();
    }

    static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is)
                .useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}



