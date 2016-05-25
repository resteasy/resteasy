/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import se.unlogic.standardutils.annotations.UnsupportedFieldTypeException;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.ManyToOne;
import se.unlogic.standardutils.populators.BeanStringPopulator;
import se.unlogic.standardutils.populators.BeanStringPopulatorRegistery;
import se.unlogic.standardutils.populators.QueryParameterPopulator;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.string.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DefaultManyToOneRelation<LocalType,RemoteType, RemoteKeyType> implements ManyToOneRelation<LocalType, RemoteType, RemoteKeyType>{

	private final String columnName;
	private final Field field;
	private QueryParameterPopulator<RemoteKeyType> queryParameterPopulator;
	private Method queryMethod;
	private final BeanResultSetPopulator<RemoteKeyType> remoteKeyPopulator;
	private final Field remoteKeyField;

	private final AnnotatedDAOFactory daoFactory;
	private AnnotatedDAO<RemoteType> annotatedDAO;
	private QueryParameterFactory<RemoteType,RemoteKeyType> queryParameterFactory;
	private final Class<RemoteType> remoteClass;
	private final Class<RemoteKeyType> remoteRemoteKeyClass;

	private boolean initialized;

	public DefaultManyToOneRelation(Class<LocalType> beanClass, Class<RemoteType> remoteClass, Class<RemoteKeyType> remoteKeyClass, Field field, Field remoteKeyField, DAOManaged daoManaged, AnnotatedDAOFactory daoFactory) {

		this.remoteClass = remoteClass;
		this.remoteRemoteKeyClass = remoteKeyClass;
		this.field = field;
		this.remoteKeyField = remoteKeyField;
		this.daoFactory = daoFactory;

		if(!StringUtils.isEmpty(daoManaged.columnName())){
			this.columnName = daoManaged.columnName();
		}else{
			this.columnName = field.getName();
		}

		ReflectionUtils.fixFieldAccess(remoteKeyField);

		//TODO use column instead! somehow...
		Method resultSetMethod = ResultSetMethods.getColumnNameMethod(remoteKeyClass);

		if(resultSetMethod != null){
			
			remoteKeyPopulator = new MethodBasedResultSetPopulator<RemoteKeyType>(resultSetMethod, columnName);
			
		}else{
			
			BeanStringPopulator<RemoteKeyType> typePopulator = BeanStringPopulatorRegistery.getBeanStringPopulator(remoteKeyClass);
			
			if(typePopulator != null){
				
				remoteKeyPopulator = new TypeBasedResultSetPopulator<RemoteKeyType>(typePopulator, columnName);
			}else{

				throw new UnsupportedFieldTypeException("Unable to find resultset method or type populator for field " + remoteKeyField.getName() + " in " + remoteClass + " when creating many to one relation for field " + field.getName() + " in " + beanClass, field, ManyToOne.class, beanClass);
			}
		}
	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.ManyToOneRelation#getColumnName()
	 */
	public String getColumnName(){

		return columnName;
	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.ManyToOneRelation#getQueryParameterPopulator()
	 */
	public QueryParameterPopulator<RemoteKeyType> getQueryParameterPopulator(){

		if(queryParameterPopulator == null && queryMethod == null){

			if(!initialized){

				this.init();
			}

			this.queryParameterPopulator = annotatedDAO.getQueryParameterPopulator(remoteRemoteKeyClass);
		}

		return queryParameterPopulator;
	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.ManyToOneRelation#getQueryMethod()
	 */
	public Method getQueryMethod(){

		if(this.queryMethod == null){
			this.queryMethod = PreparedStatementQueryMethods.getObjectQueryMethod();
		}

		return queryMethod;
	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.ManyToOneRelation#getBeanValue(LocalType)
	 */
	@SuppressWarnings("unchecked")
	public RemoteKeyType getBeanValue(LocalType bean){

		try {
			RemoteType subBean = (RemoteType) field.get(bean);

			if(subBean == null){
				return null;
			}

			return (RemoteKeyType)remoteKeyField.get(subBean);

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.ManyToOneRelation#getParamValue(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public RemoteKeyType getParamValue(Object bean) {

		try {
			if(bean == null){
				return null;
			}

			return (RemoteKeyType) remoteKeyField.get(bean);

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.ManyToOneRelation#setValue(LocalType, java.sql.ResultSet, java.sql.Connection, java.lang.reflect.Field[])
	 */
	public void getRemoteValue(LocalType bean, ResultSet resultSet, Connection connection, RelationQuery relationQuery) throws SQLException{

		try {

			if(!initialized){
				this.init();
			}

			RemoteKeyType keyValue = remoteKeyPopulator.populate(resultSet);

			if(keyValue != null){

				HighLevelQuery<RemoteType> query = new HighLevelQuery<RemoteType>();

				query.addParameter(queryParameterFactory.getParameter(keyValue));
				
				if(relationQuery != null){
					query.disableAutoRelations(relationQuery.isDisableAutoRelations());
				}
				
				query.addRelations(relationQuery);

				RemoteType remoteBeanInstance = annotatedDAO.get(query ,connection);

				this.getField().set(bean, remoteBeanInstance);
			}

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}



	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.ManyToOneRelation#add(LocalType, java.sql.Connection, java.lang.reflect.Field)
	 */
	@SuppressWarnings("unchecked")
	public void add(LocalType bean, Connection connection, RelationQuery relationQuery) throws SQLException{

		if(!initialized){
			this.init();
		}

		try {
			RemoteType remoteBean = (RemoteType) field.get(bean);

			if(remoteBean != null){

				this.annotatedDAO.add(remoteBean, connection, relationQuery);
			}

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}

	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.ManyToOneRelation#update(LocalType, java.sql.Connection, java.lang.reflect.Field)
	 */
	@SuppressWarnings("unchecked")
	public void update(LocalType bean, Connection connection, RelationQuery relationQuery) throws SQLException{

		if(!initialized){
			this.init();
		}

		try {
			RemoteType remoteBean = (RemoteType) field.get(bean);

			if(remoteBean != null){

				this.annotatedDAO.addOrUpdate(remoteBean, connection, relationQuery);
			}

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}

	}

	private void init() {

		this.annotatedDAO = this.daoFactory.getDAO(remoteClass);
		this.queryParameterFactory = annotatedDAO.getParamFactory(remoteKeyField, remoteRemoteKeyClass);

		this.initialized = true;
	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.ManyToOneRelation#getField()
	 */
	public Field getField() {

		return field;
	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.ManyToOneRelation#getBeanField()
	 */
	public Field getBeanField() {

		return this.field;
	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.ManyToOneRelation#getParamType()
	 */
	public Class<RemoteType> getParamType() {

		return remoteClass;
	}


	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.ManyToOneRelation#isAutoGenerated()
	 */
	public boolean isAutoGenerated() {

		return false;
	}

	public static <LT,RT,RKT> DefaultManyToOneRelation<LT, RT, RKT> getGenericInstance(Class<LT> beanClass, Class<RT> remoteClass, Class<RKT> remoteKeyClass, Field field, Field remoteField, DAOManaged daoManaged, AnnotatedDAOFactory daoFactory){

		return new DefaultManyToOneRelation<LT, RT, RKT>(beanClass, remoteClass, remoteKeyClass, field, remoteField, daoManaged, daoFactory);
	}
}
