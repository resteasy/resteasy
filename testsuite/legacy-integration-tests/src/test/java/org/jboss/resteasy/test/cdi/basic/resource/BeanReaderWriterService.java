package org.jboss.resteasy.test.cdi.basic.resource;

import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class BeanReaderWriterService {
    private static Logger logger = Logger.getLogger(BeanReaderWriterService.class);
    @Inject
    BeanReaderWriterConfigBean bean;

    /**
     * Tests to make sure that a CDI bean was injected and that the BeanReaderWriterXFormat provider overrides the default XML provider
     */
    @GET
    @Produces("application/xml")
    public BeanReaderWriterXFormat get() {
        if (bean != null) {
            logger.info("BeanReaderWriterConfigBean version: " + bean.version());
        } else {
            throw new RuntimeException("CDI Bean Injection didn't work for test!");
        }
        return new BeanReaderWriterXFormat("foo");
    }
}
