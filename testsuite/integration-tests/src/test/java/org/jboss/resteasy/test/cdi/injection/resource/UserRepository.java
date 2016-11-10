package org.jboss.resteasy.test.cdi.injection.resource;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.enterprise.context.ApplicationScoped;

/**
 * The UserRepository
 *
 */
@ApplicationScoped
public class UserRepository {

    /**
     * The entity manager provider
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Constructor
     */
    public UserRepository() {
    }

    /**
     * Returns the entity manager for the persistence context
     *
     * @return the entity manager
     */
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    /**
     * Return an object by the given id
     *
     * @param id the {@link Object } id
     * @return A persistent instance with a given id.
     */
    public ApplicationUser find(Object id) {
        return this.getEntityManager().find(ApplicationUser.class, id);
    }

}
