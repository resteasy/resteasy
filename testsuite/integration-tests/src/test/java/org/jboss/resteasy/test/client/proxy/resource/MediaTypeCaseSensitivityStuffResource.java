package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.MessageBodyReader;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.jupiter.api.Assertions;

@Path("/stuff")
public class MediaTypeCaseSensitivityStuffResource {
    @POST
    public void post(MediaTypeCaseSensitivityStuff stuff) {
        Assertions.assertEquals(stuff.getName(), "bill");
    }

    @GET
    public void get() {
        ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        MessageBodyReader<MediaTypeCaseSensitivityStuff> messageBodyReader = factory.getMessageBodyReader(
                MediaTypeCaseSensitivityStuff.class,
                MediaTypeCaseSensitivityStuff.class, null, new MediaType("ApplIcAtion", "STufF"));
        Assertions.assertNotNull(messageBodyReader, "RESTEasy generate wrong messageBodyReader");
        Assertions.assertEquals(MediaTypeCaseSensitivityStuffProvider.class,
                messageBodyReader.getClass(),
                "RESTEasy generate wrong messageBodyReader");
    }
}
