package org.jboss.resteasy.test.providers.jaxb.resource;

import java.io.ByteArrayInputStream;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.providers.jaxb.JaxbMarshallingSoakTest;
import org.junit.jupiter.api.Assertions;

@Path("/mpac")
public class JaxbMarshallingSoakAsyncService {
    @GET()
    @Produces("text/plain")
    public String sayHello() {
        return "Hello World!";
    }

    public void addSchedule(JaxbMarshallingSoakItem item) {
        try {
            Assertions.assertNotNull(item);
            item.toString();
            JaxbMarshallingSoakTest.counter.incrementAndGet();
        } finally {
            JaxbMarshallingSoakTest.latch.countDown();
        }
    }

    @POST()
    @Path("/add")
    @Consumes("application/xml")
    public void addSchedule(byte[] bytes) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        JaxbMarshallingSoakItem item = (JaxbMarshallingSoakItem) JaxbMarshallingSoakTest.ctx.createUnmarshaller()
                .unmarshal(bais);
        try {
            addSchedule(item);
        } catch (Exception ex) {
            String str = new String(bytes);
            String msg = "Failed ";
            if (!str.equals(JaxbMarshallingSoakTest.itemString)) {
                msg += " with " + str;
            }
            throw new Exception(msg);
        }
    }

}
