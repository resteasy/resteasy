package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.resteasy.core.ExceptionAdapter;
import org.jboss.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.concurrent.ConcurrentHashMap;

public final class JAXBCache {

    private static final Logger logger = Logger.getLogger(JAXBCache.class);

    private static JAXBCache instance = new JAXBCache();

    private ConcurrentHashMap<Object, JAXBContext> contextCache = new ConcurrentHashMap<Object, JAXBContext>();

    private JAXBCache() {

    }

    public static JAXBCache instance() {
        return instance;
    }

    public JAXBContext getJAXBContext(Class<?>... classes) {
        JAXBContext context = contextCache.get(classes);
        if (context == null) {
            try {
                context = JAXBContext.newInstance(classes);
            } catch (JAXBException e) {
                throw new ExceptionAdapter(e);
            }
            contextCache.putIfAbsent(classes, context);
        }
        logger.debugv("Locating JAXBContext for package: {0}", (Object[]) classes);
        return context;
    }

    /*public JAXBContext getJAXBContext(String... packageNames) {
        String contextPath = buildContextPath(packageNames);
        logger.debug("Locating JAXBContext for packages: {0}", contextPath);
        // FIXME This was the original call causing an infinitive recursive loop.
        // However I don't know how to fix it, but this method is not used currently
        // so instead of fixing it modified it to return a null and not going into
        // recursive loop for now.

        // return getJAXBContext(contextPath, null);
        return null;
    }*/

    private String buildContextPath(String[] packageNames) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < packageNames.length; i++) {
            b.append(packageNames[i]);
            if (i != (packageNames.length - 1)) {
                b.append(":");
            }
        }
        return b.toString();
    }

}
