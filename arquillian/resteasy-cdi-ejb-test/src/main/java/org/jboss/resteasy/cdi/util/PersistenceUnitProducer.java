package org.jboss.resteasy.cdi.util;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.resteasy.cdi.injection.ResourceBinding;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jun 11, 2012
 */
public class PersistenceUnitProducer
{
   @Produces
   @ResourceBinding
   @PersistenceContext(unitName="test")
   EntityManager persistenceContext;
}
