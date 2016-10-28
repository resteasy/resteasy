package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Path("")
public class XmlJavaTypeAdapterResource {

    private static Logger logger = Logger.getLogger(XmlJavaTypeAdapterResource.class.getName());

    @POST
    @Path("foo/foo")
    @Consumes("application/xml")
    @Produces("application/xml")
    public XmlJavaTypeAdapterFoo foo(XmlJavaTypeAdapterFoo foo) {
        logger.info("foo: \"" + foo + "\"");
        return foo;
    }

    @POST
    @Path("human")
    @Produces("text/plain")
    public String human(XmlJavaTypeAdapterHuman human) {
        logger.info("human: \"" + human.getName() + "\"");
        return human.getName();
    }

    @POST
    @Path("alien")
    @Produces("text/plain")
    public String alien(XmlJavaTypeAdapterAlien alien) {
        logger.info("human: \"" + alien.getName() + "\"");
        return alien.getName();
    }

    @POST
    @Path("list/alien")
    @Consumes("application/xml")
    @Produces("application/xml")
    public List<XmlJavaTypeAdapterAlien> listAlien(List<XmlJavaTypeAdapterAlien> list) {
        logger.info("entering listAlien()");
        return list;
    }

    @POST
    @Path("array/alien")
    @Consumes("application/xml")
    @Produces("application/xml")
    public XmlJavaTypeAdapterAlien[] arrayAlien(XmlJavaTypeAdapterAlien[] array) {
        logger.info("entering arrayAlien()");
        return array;
    }

    @POST
    @Path("map/alien")
    @Consumes("application/xml")
    @Produces("application/xml")
    public Map<String, XmlJavaTypeAdapterAlien> mapAlien(Map<String, XmlJavaTypeAdapterAlien> map) {
        logger.info("entering mapAlien()");
        return map;
    }

    @POST
    @Path("list/human")
    @Consumes("application/xml")
    @Produces("text/plain")
    public String listHuman(List<XmlJavaTypeAdapterHuman> list) {
        String result = "";
        for (Iterator<XmlJavaTypeAdapterHuman> it = list.iterator(); it.hasNext(); ) {
            String name = it.next().getName();
            result += "|" + name;
        }
        return result;
    }
}
