package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.resteasy.test.providers.jaxb.JaxbMarshallingSoakTest;
import org.junit.Assert;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.ByteArrayInputStream;

@Path("/mpac")
public class JaxbMarshallingSoakAsyncService {
    @GET()
    @Produces("text/plain")
    public String sayHello() {
        return "Hello World!";
    }


    public void addSchedule(JaxbMarshallingSoakItem item) {
        try {
            Assert.assertNotNull(item);
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
        JaxbMarshallingSoakItem item = (JaxbMarshallingSoakItem) JaxbMarshallingSoakTest.ctx.createUnmarshaller().unmarshal(bais);
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
