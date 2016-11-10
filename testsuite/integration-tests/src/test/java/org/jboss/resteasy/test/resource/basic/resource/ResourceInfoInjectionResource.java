package org.jboss.resteasy.test.resource.basic.resource;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Path("")
public class ResourceInfoInjectionResource {
    @Context
    private HttpServletRequest request;

    @GET
    @Path("test")
    public String test() {
        return "abc";
    }

    @POST
    @Path("async")
    public void async(@Suspended final AsyncResponse async) throws IOException {
        final ServletInputStream inputStream = request.getInputStream();
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        inputStream.setReadListener(new ReadListener() {
            @Override
            public void onDataAvailable() throws IOException {
                // copy input stream
                byte[] buffer = new byte[4096];
                int n1;
                while (-1 != (n1 = inputStream.read(buffer))) {
                    outputStream.write(buffer, 0, n1);
                }
            }

            @Override
            public void onAllDataRead() throws IOException {
                inputStream.close();
                outputStream.flush();
                outputStream.close();
                async.resume(outputStream.toString(StandardCharsets.UTF_8.name()));
            }

            @Override
            public void onError(Throwable t) {
                async.resume(t);
            }
        });
    }
}
