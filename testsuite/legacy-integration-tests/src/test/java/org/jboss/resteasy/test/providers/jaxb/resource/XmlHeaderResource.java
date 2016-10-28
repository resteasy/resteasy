package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.resteasy.annotations.providers.jaxb.Stylesheet;
import org.jboss.resteasy.annotations.providers.jaxb.XmlHeader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/test")
public class XmlHeaderResource {

    @GET
    @Path("/header")
    @Produces("application/xml")
    @XmlHeader("<?xml-stylesheet type='text/xsl' href='${baseuri}foo.xsl' ?>")
    public XmlHeaderThing get() {
        XmlHeaderThing thing = new XmlHeaderThing();
        thing.setName("bill");
        return thing;
    }

    @GET
    @Path("/stylesheet")
    @Produces("application/xml")
    @Stylesheet(type = "text/css", href = "${basepath}foo.xsl")
    @XmlHeaderJunkIntf
    public XmlHeaderThing getStyle() {
        XmlHeaderThing thing = new XmlHeaderThing();
        thing.setName("bill");
        return thing;
    }
}
