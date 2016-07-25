package org.jboss.resteasy.test.cdi.extensions.resource;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

public class ScopeExtensionPlannedObsolescenceExtension implements Extension {
    void beforeBeanDiscovery(@Observes BeforeBeanDiscovery event) {
        event.addScope(ScopeExtensionPlannedObsolescenceScope.class, true, false);
    }

    void afterBeanDiscovery(@Observes AfterBeanDiscovery event) {
        event.addContext(new ScopeExtensionPlannedObsolescenceContext());
    }
}

