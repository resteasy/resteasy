package org.jboss.resteasy.test.providers.jackson.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/jaxb")
public class JacksonJAXBResource {

    @GET
    @Produces("application/json")
    public JacksonXmlResourceWithJAXB getJAXBResource() {
        JacksonXmlResourceWithJAXB resourceWithJAXB = new JacksonXmlResourceWithJAXB();
        resourceWithJAXB.setAttr1("XXX");
        resourceWithJAXB.setAttr2("YYY");
        return resourceWithJAXB;
    }


    @GET
    @Path(("/json"))
    @Produces("application/json")
    public JacksonXmlResourceWithJacksonAnnotation getJacksonAnnotatedResource() {
        JacksonXmlResourceWithJacksonAnnotation resource = new JacksonXmlResourceWithJacksonAnnotation();
        resource.setAttr1("XXX");
        resource.setAttr2("YYY");
        return resource;
    }

}
