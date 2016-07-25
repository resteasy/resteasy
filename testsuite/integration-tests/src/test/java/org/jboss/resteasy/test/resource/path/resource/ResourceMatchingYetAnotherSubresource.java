package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("yas")
public class ResourceMatchingYetAnotherSubresource {
    @GET
    @Produces("text/*")
    public String getTextStar() {
        return "text/*";
    }

    @POST
    @Consumes("text/*")
    public String postTextStar() {
        return "text/*";
    }

    @POST
    @Consumes("text/xml;qs=0.7")
    public String xml() {
        return MediaType.TEXT_XML;
    }

    @GET
    @Produces("text/xml;qs=0.7")
    public String xmlGet() {
        return MediaType.TEXT_XML;
    }

    @GET
    @Produces("application/xml;qs=0.8")
    public String appXmlGet() {
        return MediaType.APPLICATION_XML;
    }

    @GET
    @Produces("testiii/textiii;qs=0.7")
    public String testiiiTextiiiGet() {
        return "testiii/textiii";
    }


    @GET
    @Produces("testi/*")
    public String testStar() {
        return "test/*";
    }

    @GET
    @Produces("testi/text")
    public String testText() {
        return "test/text";
    }

    @GET
    @Produces("testii/texta")
    public String testIITextA() {
        return "textA";
    }

    @GET
    @Produces("testii/textb")
    public String testIITextB() {
        return "textB";
    }

}
