package org.jboss.resteasy.test.core.spi.resource;

import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceClassProcessor;
import javax.ws.rs.ext.Provider;

@Provider
public class ResourceClassProcessorImplementation implements ResourceClassProcessor {
    @Override
    public ResourceClass process(ResourceClass clazz) {
        String clazzName = clazz.getClazz().getSimpleName();
        if (clazzName.equals("ResourceClassProcessorEndPoint")
                || clazzName.startsWith("ResourceClassProcessorProxy")) {
            return new ResourceClassProcessorClass(clazz);
        }
        return clazz;
    }
}
