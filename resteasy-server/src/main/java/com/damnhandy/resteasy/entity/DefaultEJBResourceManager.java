package com.damnhandy.resteasy.entity;

import java.beans.BeanInfo;

//@Local
public interface DefaultEJBResourceManager extends ResourceManager {

	/**
	 * @return the beanInfo
	 */
	public abstract BeanInfo getBeanInfo();

	/**
	 * @param beanInfo the beanInfo to set
	 */
	public abstract void setBeanInfo(BeanInfo beanInfo);

	/**
	 * @return the entityClass
	 */
	public abstract Class<?> getEntityClass();

	/**
	 * @param entityClass the entityClass to set
	 */
	public abstract void setEntityClass(Class<?> entityClass);

}