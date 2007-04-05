/**
 * 
 */
package com.damnhandy.resteasy.entity;

import java.beans.BeanInfo;
import java.io.Serializable;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.damnhandy.resteasy.representation.JAXBRepresentation;
import com.damnhandy.resteasy.representation.Representation;

/**
 * @author Ryan J. McDonough
 * @since 1.0
 *
 */
//@Stateless
//@Name("defaultResourceManager")
public class DefaultEJBResourceManagerBean implements DefaultEJBResourceManager {

	private BeanInfo beanInfo;
	private Class<?> entityClass;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	/**
	 * 
	 * @return
	 */
	private EntityManager getEntityManager() {
		//EntityManager entityManager = (EntityManager) Component.forName("");
		return entityManager;
	}
	
	/* (non-Javadoc)
	 * @see com.damnhandy.resteasy.entity.DefaultPersister#getBeanInfo()
	 */
	public BeanInfo getBeanInfo() {
		return beanInfo;
	}

	/* (non-Javadoc)
	 * @see com.damnhandy.resteasy.entity.DefaultPersister#setBeanInfo(java.beans.BeanInfo)
	 */
	public void setBeanInfo(BeanInfo beanInfo) {
		this.beanInfo = beanInfo;
	}

	/* (non-Javadoc)
	 * @see com.damnhandy.resteasy.entity.DefaultPersister#getEntityClass()
	 */
	public Class<?> getEntityClass() {
		return entityClass;
	}

	/* (non-Javadoc)
	 * @see com.damnhandy.resteasy.entity.DefaultPersister#setEntityClass(java.lang.Class)
	 */
	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}


	/* (non-Javadoc)
	 * @see com.damnhandy.resteasy.entity.DefaultPersister#createEntity(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public Representation createEntity(Serializable entity) {
		getEntityManager().persist(entity);
		Representation rep = new JAXBRepresentation(entity);
		return rep;
	}

	/* (non-Javadoc)
	 * @see com.damnhandy.resteasy.entity.DefaultPersister#remove(java.io.Serializable)
	 */
	public void remove(Serializable id) {
		getEntityManager().remove(id);
	}

	
	/* (non-Javadoc)
	 * @see com.damnhandy.resteasy.entity.DefaultPersister#findEntityByProperty(java.util.Map)
	 */
	public Representation findEntityByProperty(Map queryParameters) {
		//StringBuilder b = new StringBuilder();
		/*
		 * TODO: Use the BeanInfo to extract the valid entity properties and generate a Query
		 */
		return null;
	}

	/* (non-Javadoc)
	 * @see com.damnhandy.resteasy.entity.DefaultPersister#getEntityById(java.io.Serializable)
	 */
	@SuppressWarnings("unchecked")
	public Representation getEntityById(Serializable id) {
		Serializable entity = (Serializable) getEntityManager().find(getEntityClass(), id);
		Representation rep = new JAXBRepresentation(entity);
		return rep;
	}

	/* (non-Javadoc)
	 * @see com.damnhandy.resteasy.entity.DefaultPersister#updateEntity(java.io.Serializable, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public Representation updateEntity(Serializable id, Serializable entity) {
		entity = getEntityManager().merge(entity);
		Representation rep = new JAXBRepresentation(entity);
		return rep;
	}

}
