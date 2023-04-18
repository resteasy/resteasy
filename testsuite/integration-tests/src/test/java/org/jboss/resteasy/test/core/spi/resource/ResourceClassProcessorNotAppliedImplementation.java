package org.jboss.resteasy.test.core.spi.resource;

import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceClassProcessor;
import org.jboss.resteasy.test.core.spi.ResourceClassProcessorNotAppliedTest;

@Provider
public class ResourceClassProcessorNotAppliedImplementation implements ResourceClassProcessor {
    protected static final Logger logger = Logger.getLogger(ResourceClassProcessorNotAppliedImplementation.class.getName());

    @Override
    public ResourceClass process(ResourceClass clazz) {
        logger.info("ResourceClassProcessorNotAppliedImplementation visited on server");
        ResourceClassProcessorNotAppliedTest.addToVisitedProcessors("A");
        return clazz;
    }
}
