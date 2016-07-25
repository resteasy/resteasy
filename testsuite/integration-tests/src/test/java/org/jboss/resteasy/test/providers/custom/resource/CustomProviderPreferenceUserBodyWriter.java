package org.jboss.resteasy.test.providers.custom.resource;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces("text/plain")
public class CustomProviderPreferenceUserBodyWriter implements MessageBodyWriter<CustomProviderPreferenceUser> {

    public long getSize(CustomProviderPreferenceUser arg0, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4) {
        return getStringRepresentation(arg0).length();
    }

    public boolean isWriteable(Class<?> arg0, Type arg1, Annotation[] arg2, MediaType arg3) {
        return true;
    }

    public void writeTo(CustomProviderPreferenceUser arg0, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4,
                        MultivaluedMap<String, Object> arg5, OutputStream arg6) throws IOException, WebApplicationException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(arg6));
        bw.write(getStringRepresentation(arg0));
        bw.flush();
    }

    private String getStringRepresentation(CustomProviderPreferenceUser user) {
        return user.getUsername() + ";" + user.getEmail();
    }
}
