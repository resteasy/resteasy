package org.jboss.resteasy.test.core.spi.resource;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceClassProcessor;
import org.jboss.resteasy.test.core.spi.ResourceClassProcessorPriorityTest;

@Provider
@Priority(40)
public class ResourceClassProcessorPriiorityAImplementation implements ResourceClassProcessor {
    protected static final Logger logger = Logger.getLogger(ResourceClassProcessorPriiorityAImplementation.class.getName());

    @Override
    public ResourceClass process(ResourceClass clazz) {
        logger.info("ResourceClassProcessorPriiorityAImplementation visited on server");
        ResourceClassProcessorPriorityTest.addToVisitedProcessors("A");
        return clazz;
    }
}
