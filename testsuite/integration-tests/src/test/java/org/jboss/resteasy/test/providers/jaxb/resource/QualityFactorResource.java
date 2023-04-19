package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.logging.Logger;

@Path("/test")
public class QualityFactorResource {

    private static Logger logger = Logger.getLogger(QualityFactorResource.class.getName());

    @GET
    @Produces({ "application/json", "application/xml" })
    public QualityFactorThing get(@HeaderParam("Accept") String accept) {
        logger.info(accept);
        QualityFactorThing thing = new QualityFactorThing();
        thing.setName("Bill");
        return thing;
    }
}
