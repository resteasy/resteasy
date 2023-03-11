package org.jboss.resteasy.test.resource.basic.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.Context;

import org.jboss.logging.Logger;

@Path("")
public class ResourceInfoInjectionResource {
    protected static final Logger logger = Logger.getLogger(ResourceInfoInjectionResource.class.getName());

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
        logger.info("Start async");
        final ServletInputStream inputStream = request.getInputStream();
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        inputStream.setReadListener(new ReadListener() {
            @Override
            public void onDataAvailable() throws IOException {
                logger.info("Start onDataAvailable");
                // copy input stream
                byte[] buffer = new byte[4096];
                int n1;
                while (inputStream.isReady()) {
                    n1 = inputStream.read(buffer);
                    outputStream.write(buffer, 0, n1);
                }
                logger.info("End onDataAvailable");
            }

            @Override
            public void onAllDataRead() throws IOException {
                logger.info("Start onAllDataRead");
                inputStream.close();
                outputStream.flush();
                outputStream.close();
                async.resume(outputStream.toString(StandardCharsets.UTF_8.name()));
                logger.info("End onAllDataRead");
            }

            @Override
            public void onError(Throwable t) {
                logger.info("Start onError");
                async.resume(t);
                logger.info("End onError");
            }
        });
        logger.info("End async");
    }
}
