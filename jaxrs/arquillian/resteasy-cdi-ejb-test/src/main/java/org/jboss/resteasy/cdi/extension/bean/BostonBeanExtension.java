package org.jboss.resteasy.cdi.extension.bean;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

/**
 * BostonBeanExtension implements a CDI extension.
 * 
 * In particular, it creates a BostonBean for each of the two classes,
 * BostonHolder and BostonLeaf, that are annotated with @Boston, and it
 * registers them with the CDI runtime.
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jun 11, 2012
 */
public class BostonBeanExtension implements Extension
{   
   @SuppressWarnings({ "rawtypes", "unchecked" })
   void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager bm)
   {
      System.out.println("AfterBeanDiscovery");
      BostonBean<?> rb = new BostonBean(BostonHolder.class, bm.createInjectionTarget(bm.createAnnotatedType(BostonHolder.class)));
      abd.addBean(rb);
      System.out.println("registered " + rb.toString());
      
      rb = new BostonBean(BostonlLeaf.class, bm.createInjectionTarget(bm.createAnnotatedType(BostonlLeaf.class)));
      abd.addBean(rb);
      System.out.println("registered " + rb.toString());
   }
}
