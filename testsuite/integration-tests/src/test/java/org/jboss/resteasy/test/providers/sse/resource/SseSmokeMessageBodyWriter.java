package org.jboss.resteasy.test.providers.sse.resource;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.api.validation.ConstraintType;

@Provider
@Produces("text/plain")
public class SseSmokeMessageBodyWriter implements MessageBodyWriter<SseSmokeUser> {

    public long getSize(SseSmokeUser arg0, Class<?> arg1, ConstraintType.Type arg2, Annotation[] arg3, MediaType arg4) {
        return getStringRepresentation(arg0).length();
    }

    public boolean isWriteable(Class<?> arg0, Type arg1, Annotation[] arg2, MediaType arg3) {
        return true;
    }

    public void writeTo(SseSmokeUser arg0, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4,
            MultivaluedMap<String, Object> arg5, OutputStream arg6) throws IOException, WebApplicationException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(arg6, StandardCharsets.UTF_8));
        bw.write(getStringRepresentation(arg0));
        bw.flush();
    }

    private String getStringRepresentation(SseSmokeUser user) {
        return user.getUsername() + ";" + user.getEmail();
    }
}
