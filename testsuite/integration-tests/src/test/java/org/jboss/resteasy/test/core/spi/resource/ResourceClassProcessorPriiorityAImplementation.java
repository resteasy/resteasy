package org.jboss.resteasy.test.core.spi.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceClassProcessor;
import org.jboss.resteasy.test.core.spi.ResourceClassProcessorPriorityTest;

import javax.annotation.Priority;
import javax.ws.rs.ext.Provider;

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
