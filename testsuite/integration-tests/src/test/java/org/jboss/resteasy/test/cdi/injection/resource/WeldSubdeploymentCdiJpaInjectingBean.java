package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;

@Dependent
public class WeldSubdeploymentCdiJpaInjectingBean {

    @Produces
    @PersistenceUnit(unitName = "cdiPu")
    EntityManagerFactory emf;

    public EntityManagerFactory entityManagerFactory() {
        return emf;
    }

}
