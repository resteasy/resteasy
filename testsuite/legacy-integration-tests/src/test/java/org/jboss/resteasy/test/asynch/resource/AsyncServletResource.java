package org.jboss.resteasy.test.asynch.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.Suspend;
import org.jboss.resteasy.spi.AsynchronousResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/async")
public class AsyncServletResource {
    @GET
    @Produces("text/plain")
    public void get(@Suspend(2000) final AsynchronousResponse response) {
        Thread t = new Thread() {
            private Logger log = Logger.getLogger(AsyncServletResource.class);
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                    Response jaxrs = Response.ok("hello").type(MediaType.TEXT_PLAIN).build();
                    response.setResponse(jaxrs);
                } catch (Exception e) {
                    StringWriter errors = new StringWriter();
                    e.printStackTrace(new PrintWriter(errors));
                    log.error(errors.toString());
                }
            }
        };
        t.start();
    }

    @GET
    @Path("timeout")
    @Produces("text/plain")
    public void timeout(@Suspend(10) final AsynchronousResponse response) {
        Thread t = new Thread() {
            private Logger log = Logger.getLogger(AsyncServletResource.class);
            @Override
            public void run() {
                try {
                    Thread.sleep(100000);
                    Response jaxrs = Response.ok("goodbye").type(MediaType.TEXT_PLAIN).build();
                    response.setResponse(jaxrs);
                } catch (Exception e) {
                    StringWriter errors = new StringWriter();
                    e.printStackTrace(new PrintWriter(errors));
                    log.error(errors.toString());
                }
            }
        };
        t.start();
    }
}