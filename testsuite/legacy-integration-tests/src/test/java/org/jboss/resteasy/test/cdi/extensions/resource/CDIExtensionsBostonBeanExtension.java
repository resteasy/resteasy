package org.jboss.resteasy.test.cdi.extensions.resource;

import org.jboss.logging.Logger;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

/**
 * BostonBeanExtension implements a CDI extension.
 * In particular, it creates a BostonBean for each of the two classes,
 * BostonHolder and BostonLeaf, that are annotated with @Boston, and it
 * registers them with the CDI runtime.
 */
public class CDIExtensionsBostonBeanExtension implements Extension {
    private static Logger log = Logger.getLogger(CDIExtensionsBostonBeanExtension.class);

    @SuppressWarnings({"rawtypes", "unchecked"})
    void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager bm) {
        log.info("AfterBeanDiscovery");
        CDIExtensionsBostonBean<?> rb = new CDIExtensionsBostonBean(CDIExtensionsBostonHolder.class, bm.createInjectionTarget(bm.createAnnotatedType(CDIExtensionsBostonHolder.class)));
        abd.addBean(rb);
        log.info("registered " + rb.toString());

        rb = new CDIExtensionsBostonBean(CDIExtensionsBostonlLeaf.class, bm.createInjectionTarget(bm.createAnnotatedType(CDIExtensionsBostonlLeaf.class)));
        abd.addBean(rb);
        log.info("registered " + rb.toString());
    }
}
