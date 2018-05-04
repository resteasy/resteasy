package org.jboss.resteasy.test.core.spi.resource;

import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceClassProcessor;

import javax.ws.rs.ext.Provider;

@Provider
public class ResourceClassProcessorErrorImplementation implements ResourceClassProcessor {
    @Override
    public ResourceClass process(ResourceClass clazz) {
        throw new RuntimeException("Exception from ResourceClassProcessorErrorImplementation");
    }
}
