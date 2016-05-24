/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import se.unlogic.standardutils.populators.BeanStringPopulator;
import se.unlogic.standardutils.populators.QueryParameterPopulator;
import se.unlogic.standardutils.populators.annotated.AnnotatedResultSetPopulator;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;


public class SimpleAnnotatedDAOFactory implements AnnotatedDAOFactory{

	private final DataSource dataSource;
	private final HashMap<Class<?>, AnnotatedDAO<?>> daoMap = new HashMap<Class<?>, AnnotatedDAO<?>>();

	public SimpleAnnotatedDAOFactory(DataSource dataSource) {

		super();
		this.dataSource = dataSource;
	}

	public SimpleAnnotatedDAOFactory() {

		super();
		this.dataSource = null;
	}

	@SuppressWarnings("unchecked")
	public synchronized <T> AnnotatedDAO<T> getDAO(Class<T> beanClass) {

		AnnotatedDAO<T> dao = (AnnotatedDAO<T>) this.daoMap.get(beanClass);

		if(dao == null){

			dao = new AnnotatedDAO<T>(dataSource, beanClass, this);
			this.daoMap.put(beanClass, dao);
		}

		return dao;
	}

	@SuppressWarnings("unchecked")
	public synchronized <T> AnnotatedDAO<T> getDAO(Class<T> beanClass, AnnotatedResultSetPopulator<T> populator) {

		AnnotatedDAO<T> dao = (AnnotatedDAO<T>) this.daoMap.get(beanClass);

		if(dao == null){

			dao = new AnnotatedDAO<T>(dataSource, beanClass, this, populator, null, null);
			this.daoMap.put(beanClass, dao);
		}

		return dao;
	}

	@SuppressWarnings("unchecked")
	public synchronized <T> AnnotatedDAO<T> getDAO(Class<T> beanClass, AnnotatedResultSetPopulator<T> populator, QueryParameterPopulator<?>... queryParameterPopulators) {

		AnnotatedDAO<T> dao = (AnnotatedDAO<T>) this.daoMap.get(beanClass);

		if(dao == null){

			dao = new AnnotatedDAO<T>(dataSource, beanClass, this, populator, queryParameterPopulators);
			this.daoMap.put(beanClass, dao);
		}

		return dao;
	}

	@SuppressWarnings("unchecked")
	public synchronized <T> AnnotatedDAO<T> getDAO(Class<T> beanClass, List<? extends QueryParameterPopulator<?>> queryParameterPopulators, List<? extends BeanStringPopulator<?>> typePopulators) {

		AnnotatedDAO<T> dao = (AnnotatedDAO<T>) this.daoMap.get(beanClass);

		if(dao == null){

			dao = new AnnotatedDAO<T>(dataSource, beanClass, this, queryParameterPopulators, typePopulators);
			this.daoMap.put(beanClass, dao);
		}

		return dao;
	}	
	
	@SuppressWarnings("unchecked")
	public synchronized <T> AnnotatedDAO<T> getDAO(Class<T> beanClass, AnnotatedResultSetPopulator<T> populator, List<? extends QueryParameterPopulator<?>> queryParameterPopulators, List<? extends BeanStringPopulator<?>> typePopulators) {

		AnnotatedDAO<T> dao = (AnnotatedDAO<T>) this.daoMap.get(beanClass);

		if(dao == null){

			dao = new AnnotatedDAO<T>(dataSource, beanClass, this, populator, queryParameterPopulators, typePopulators);
			this.daoMap.put(beanClass, dao);
		}

		return dao;
	}

	public DataSource getDataSource() {

		return dataSource;
	}

	public void addDAO(Class<?> beanClass, AnnotatedDAO<?> daoInstance) {
		daoMap.put(beanClass, daoInstance);
	}
}
