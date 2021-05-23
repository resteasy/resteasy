package org.jboss.resteasy.test.cdi.extensions.resource;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;

public class ScopeExtensionPlannedObsolescenceExtension implements Extension {
   void beforeBeanDiscovery(@Observes BeforeBeanDiscovery event) {
      event.addScope(ScopeExtensionPlannedObsolescenceScope.class, true, false);
   }

   void afterBeanDiscovery(@Observes AfterBeanDiscovery event) {
      event.addContext(new ScopeExtensionPlannedObsolescenceContext());
   }
}
