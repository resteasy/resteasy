package org.jboss.resteasy.test.client.proxy.resource;

import static org.hamcrest.core.IsNull.notNullValue;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.MessageBodyReader;

import org.hamcrest.MatcherAssert;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Assert;

@Path("/stuff")
public class MediaTypeCaseSensitivityStuffResource {
    @POST
    public void post(MediaTypeCaseSensitivityStuff stuff) {
        Assert.assertEquals(stuff.getName(), "bill");
    }

    @GET
    public void get() {
        ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        MessageBodyReader<MediaTypeCaseSensitivityStuff> messageBodyReader = factory.getMessageBodyReader(
                MediaTypeCaseSensitivityStuff.class,
                MediaTypeCaseSensitivityStuff.class, null, new MediaType("ApplIcAtion", "STufF"));
        MatcherAssert.assertThat("RESTEasy generate wrong messageBodyReader", messageBodyReader, notNullValue());
        MatcherAssert.assertThat("RESTEasy generate wrong messageBodyReader", messageBodyReader.getClass(), notNullValue());
        Assert.assertEquals("RESTEasy generate wrong messageBodyReader", MediaTypeCaseSensitivityStuffProvider.class,
                messageBodyReader.getClass());
    }
}
