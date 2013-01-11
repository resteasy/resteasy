package org.jboss.resteasy.cdi.extension.scope;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jun 11, 2012
 */
public class PlannedObsolescenceExtension implements Extension
{
   void beforeBeanDiscovery(@Observes BeforeBeanDiscovery event)
   {
      event.addScope(PlannedObsolescenceScope.class, true, false);
   }

   void afterBeanDiscovery(@Observes AfterBeanDiscovery event)
   {
      event.addContext(new PlannedObsolescenceContext());
   }
}

