/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.ManyToOne;
import se.unlogic.standardutils.reflection.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class DefaultOneToManyRelation<LocalType,RemoteType> implements OneToManyRelation<LocalType, RemoteType> {

	private final Field field;
	private Field remoteField;
	private final AnnotatedDAOFactory daoFactory;
	private AnnotatedDAO<RemoteType> annotatedDAO;
	private QueryParameterFactory<RemoteType, LocalType> queryParameterFactory;
	private final Class<LocalType> beanClass;
	private final Class<RemoteType> remoteClass;
	private boolean initialized;
	
	public DefaultOneToManyRelation(Class<LocalType> beanClass, Class<RemoteType> remoteClass, Field field, AnnotatedDAOFactory daoFactory, DAOManaged daoManaged) {
		super();
		this.beanClass = beanClass;
		this.remoteClass = remoteClass;
		this.field = field;
		this.daoFactory = daoFactory;
		
		List<Field> fields = ReflectionUtils.getFields(remoteClass);

		for(Field remoteField : fields){

			if(remoteField.getType().equals(beanClass) && remoteField.isAnnotationPresent(DAOManaged.class) && remoteField.isAnnotationPresent(ManyToOne.class)){
								
				this.remoteField = remoteField;

				ReflectionUtils.fixFieldAccess(this.remoteField);
				
				break;
			}
		}

		if(this.remoteField == null){

			throw new RuntimeException("Unable to to find corresponding @ManyToOne field in class " + remoteClass + " for @OneToMany annotated field " + field.getName() + " in " + beanClass);
		}
	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.OneToManyRelation#setValue(LocalType, java.sql.Connection, java.lang.reflect.Field[])
	 */
	public void getRemoteValue(LocalType bean, Connection connection, RelationQuery relationQuery) throws SQLException{

		if(!initialized){
			init();
		}

		try {
			HighLevelQuery<RemoteType> query = new HighLevelQuery<RemoteType>();
			
			query.addRelations(relationQuery);
			
			if(relationQuery != null){
				query.disableAutoRelations(relationQuery.isDisableAutoRelations());
			}
			
			query.addParameter(queryParameterFactory.getParameter(bean));
			
			field.set(bean, annotatedDAO.getAll(query, connection));

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.OneToManyRelation#add(LocalType, java.sql.Connection, java.lang.reflect.Field[])
	 */
	@SuppressWarnings("unchecked")
	public void add(LocalType bean, Connection connection, RelationQuery relationQuery) throws SQLException{
	
		if(!initialized){
			init();
		}
		
		try {
			List<RemoteType> remoteBeans = (List<RemoteType>) field.get(bean);
			
			if(remoteBeans != null){
				
				this.fixReferences(remoteBeans, bean);
				
				annotatedDAO.addAll(remoteBeans, connection, relationQuery);
			}

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}	
	
	private void fixReferences(List<RemoteType> remoteBeans, LocalType bean) throws IllegalArgumentException, IllegalAccessException {

		for(RemoteType remoteBean : remoteBeans){
			
			remoteField.set(remoteBean, bean);
		}
	}

	/* (non-Javadoc)
	 * @see se.unlogic.utils.dao.OneToManyRelation#update(LocalType, java.sql.Connection, java.lang.reflect.Field[])
	 */
	@SuppressWarnings("unchecked")
	public void update(LocalType bean, Connection connection, RelationQuery relationQuery) throws SQLException{
	
		if(!initialized){
			init();
		}
		
		try {
			List<RemoteType> remoteBeans = (List<RemoteType>) field.get(bean);
			
			if(remoteBeans == null ||  remoteBeans.isEmpty()){
				
				HighLevelQuery<RemoteType> query = new HighLevelQuery<RemoteType>();
				
				query.addRelations(relationQuery);
				
				if(relationQuery != null){
					query.disableAutoRelations(relationQuery.isDisableAutoRelations());	
				}
				
				query.addParameter(queryParameterFactory.getParameter(bean));
				
				annotatedDAO.delete(query, connection);
				
			}else{
				
				this.fixReferences(remoteBeans, bean);
				
				//TODO exclude current parameter
				
				QueryParameter<RemoteType,LocalType> queryParameter = queryParameterFactory.getParameter(bean);
				
				if(!annotatedDAO.deleteWhereNotIn(remoteBeans, connection, this.remoteField, queryParameter)){
					
					HighLevelQuery<RemoteType> query = new HighLevelQuery<RemoteType>();
					
					query.addParameter(queryParameter);
					
					annotatedDAO.delete(query, connection);
				}
				
				annotatedDAO.addOrUpdateAll(remoteBeans, connection, relationQuery);
			}

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}	
	
	private void init() {

		if(annotatedDAO == null){
			annotatedDAO = this.daoFactory.getDAO(remoteClass);
			queryParameterFactory = annotatedDAO.getParamFactory(remoteField, beanClass);
		}
		
		this.initialized = true;
	}	
	
	public static <LT,RT> OneToManyRelation<LT, RT> getGenericInstance(Class<LT> beanClass, Class<RT> remoteClass, Field field, AnnotatedDAOFactory daoFactory, DAOManaged daoManaged){

		return new DefaultOneToManyRelation<LT,RT>(beanClass,remoteClass,field,daoFactory,daoManaged);
	}
}
