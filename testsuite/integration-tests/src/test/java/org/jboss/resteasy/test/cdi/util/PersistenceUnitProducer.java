package org.jboss.resteasy.test.cdi.util;

import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionResourceBinding;

import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class PersistenceUnitProducer {
   @Produces
   @CDIInjectionResourceBinding
   @PersistenceContext(unitName = "test")
   EntityManager persistenceContext;
}
