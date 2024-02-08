package org.jboss.resteasy.test.cdi.util;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionResourceBinding;

@Dependent
public class PersistenceUnitProducer {
    @Produces
    @CDIInjectionResourceBinding
    @PersistenceContext(unitName = "test")
    EntityManager persistenceContext;
}
