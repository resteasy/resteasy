package org.jboss.resteasy.test.core.spi.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceClassProcessor;
import org.jboss.resteasy.test.core.spi.ResourceClassProcessorPriorityTest;

import javax.annotation.Priority;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(30)
public class ResourceClassProcessorPriiorityCImplementation implements ResourceClassProcessor {
    protected static final Logger logger = Logger.getLogger(ResourceClassProcessorPriiorityCImplementation.class.getName());

    @Override
    public ResourceClass process(ResourceClass clazz) {
        logger.info("ResourceClassProcessorPriiorityCImplementation visited on server");
        ResourceClassProcessorPriorityTest.addToVisitedProcessors("C");
        return clazz;
    }
}
