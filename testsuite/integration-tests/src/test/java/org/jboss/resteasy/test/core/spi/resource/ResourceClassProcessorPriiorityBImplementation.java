package org.jboss.resteasy.test.core.spi.resource;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceClassProcessor;
import org.jboss.resteasy.test.core.spi.ResourceClassProcessorPriorityTest;

@Provider
@Priority(20)
public class ResourceClassProcessorPriiorityBImplementation implements ResourceClassProcessor {
    protected static final Logger logger = Logger.getLogger(ResourceClassProcessorPriiorityBImplementation.class.getName());

    @Override
    public ResourceClass process(ResourceClass clazz) {
        logger.info("ResourceClassProcessorPriiorityBImplementation visited on server");
        ResourceClassProcessorPriorityTest.addToVisitedProcessors("B");
        return clazz;
    }
}
