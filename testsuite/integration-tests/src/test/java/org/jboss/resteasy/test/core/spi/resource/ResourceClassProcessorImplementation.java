package org.jboss.resteasy.test.core.spi.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceClassProcessor;

import javax.ws.rs.ext.Provider;

@Provider
public class ResourceClassProcessorImplementation implements ResourceClassProcessor {

    protected static final Logger logger = Logger.getLogger(ResourceClassProcessorImplementation.class.getName());

    @Override
    public ResourceClass process(ResourceClass clazz) {
        logger.info(String.format("ResourceClassProcessorImplementation#process method called on class %s",
                            clazz.getClazz().getSimpleName()));
        String clazzName = clazz.getClazz().getSimpleName();
        if (clazzName.startsWith("ResourceClassProcessorEndPoint")
                || clazzName.equals("ResourceClassProcessorProxy")
                || clazzName.equals("ResourceClassProcessorProxyEndPoint")) {
            return new ResourceClassProcessorClass(clazz);
        }
        return clazz;
    }
}
