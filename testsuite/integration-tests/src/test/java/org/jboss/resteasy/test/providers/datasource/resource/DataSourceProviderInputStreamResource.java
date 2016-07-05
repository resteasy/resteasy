package org.jboss.resteasy.test.providers.datasource.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class DataSourceProviderInputStreamResource {

    public static final int KBs = 5;
    public static final int SIZE = KBs * 1024;

    @GET
    @Produces("text/plain")
    public String get() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < SIZE; i++) {
            buffer.append("x");
        }
        return buffer.toString();
    }

}
