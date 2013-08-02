/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.SimplifiedRelation;
import se.unlogic.standardutils.dao.enums.Order;
import se.unlogic.standardutils.dao.querys.ArrayListQuery;
import se.unlogic.standardutils.dao.querys.PreparedStatementQuery;
import se.unlogic.standardutils.dao.querys.UpdateQuery;
import se.unlogic.standardutils.populators.BeanStringPopulator;
import se.unlogic.standardutils.populators.QueryParameterPopulator;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.string.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SimplifiedOneToManyRelation<LocalType, RemoteType> implements OneToManyRelation<LocalType, RemoteType> {

	private final AnnotatedDAO<LocalType> localDAO;
	private final Field field;

	private String selectSQL;
	private String insertSQL;
	private String deleteSQL;

	private boolean preserveListOrder;
	private String indexColumnName;
	
	private QueryParameterPopulator<RemoteType> queryParameterPopulator;
	private Method preparedStatementMethod;

	private BeanResultSetPopulator<RemoteType> beanResultSetPopulator;

	private Field keyField;
	private Column<LocalType, ?> keyColumn;

	private final String remoteTableName;
	private String remoteKeyColumnName;
	private final String remoteValueColumnName;
	private Order order;

	private boolean initialized;

	@SuppressWarnings("unchecked")
	public SimplifiedOneToManyRelation(Class<LocalType> beanClass, Class<RemoteType> remoteClass, Field field, AnnotatedDAO<LocalType> localDAO, List<? extends BeanStringPopulator<?>> typePopulators, List<? extends QueryParameterPopulator<?>> queryParameterPopulators) {

		super();
		this.localDAO = localDAO;
		this.field = field;

		SimplifiedRelation simplifiedRelation = field.getAnnotation(SimplifiedRelation.class);

		remoteKeyColumnName = simplifiedRelation.remoteKeyColumnName();
		remoteValueColumnName = simplifiedRelation.remoteValueColumnName();
		order = simplifiedRelation.order();

		if(simplifiedRelation.addTablePrefix()){
			
			if(simplifiedRelation.deplurifyTablePrefix() && localDAO.getTableName().endsWith("s")){
				
				remoteTableName = localDAO.getTableName().substring(0, localDAO.getTableName().length()-1) + simplifiedRelation.table();
				
			}else{
				
				remoteTableName = localDAO.getTableName() + simplifiedRelation.table();
			}
		}else{
			
			remoteTableName = simplifiedRelation.table();
		}
		
		if (!StringUtils.isEmpty(simplifiedRelation.keyField())) {

			try {
				keyField = ReflectionUtils.getField(beanClass, simplifiedRelation.keyField());

				if (keyField == null) {

					throw new RuntimeException("Unable to find field " + simplifiedRelation.keyField() + " in " + beanClass.getClass());
				}

			} catch (SecurityException e) {

				throw new RuntimeException(e);

			}

		} else {

			List<Field> fields = ReflectionUtils.getFields(beanClass);

			for (Field localBeanField : fields) {

				if (localBeanField.isAnnotationPresent(DAOManaged.class) && localBeanField.isAnnotationPresent(Key.class)) {

					if (this.keyField == null) {

						keyField = localBeanField;

					} else {

						throw new RuntimeException("Multiple fields marked with @Key annotation found in class " + beanClass + " therefore keyField has to set on the @SimplifiedRelation annotation of field " + field.getName());
					}
				}
			}
		}

		if (queryParameterPopulators != null) {

			for (QueryParameterPopulator<?> queryParameterPopulator : queryParameterPopulators) {

				if (queryParameterPopulator.getType().equals(remoteClass)) {

					this.queryParameterPopulator = (QueryParameterPopulator<RemoteType>) queryParameterPopulator;
				}
			}
		}

		if (this.queryParameterPopulator == null) {

			preparedStatementMethod = PreparedStatementQueryMethods.getQueryMethod(remoteClass);

			if (preparedStatementMethod == null) {

				throw new RuntimeException("Unable to to find a query parameter populator or prepared statement method matching " + remoteClass + " of @SimplfiedRelation and @OneToMany annotated field " + field.getName() + " in " + beanClass);
			}
		}

		if (typePopulators != null) {

			for (BeanStringPopulator<?> typePopulator : typePopulators) {

				if (typePopulator.getType().equals(remoteClass)) {

					beanResultSetPopulator = new TypeBasedResultSetPopulator<RemoteType>((BeanStringPopulator<RemoteType>) typePopulator, remoteValueColumnName);
				}
			}
		}

		if (beanResultSetPopulator == null) {

			Method resultSetMethod = ResultSetMethods.getColumnNameMethod(remoteClass);

			if (resultSetMethod != null) {

				beanResultSetPopulator = new MethodBasedResultSetPopulator<RemoteType>(resultSetMethod, remoteValueColumnName);

			} else {

				throw new RuntimeException("Unable to to find a type populator or resultset method matching " + remoteClass + " of @SimplfiedRelation and @OneToMany annotated field " + field.getName() + " in " + beanClass);
			}
		}
		
		if(simplifiedRelation.preserveListOrder()){
			
			if(StringUtils.isEmpty(simplifiedRelation.indexColumn())){
				
				throw new RuntimeException("Preserve list order enabled but no index column specified for @SimplifiedRelation annotated field " + field.getName() + " in " + beanClass);
			}
			
			preserveListOrder = true;
			indexColumnName = simplifiedRelation.indexColumn();
		}
	}

	private void init() {

		this.keyColumn = localDAO.getColumn(keyField);

		if (StringUtils.isEmpty(remoteKeyColumnName)) {

			remoteKeyColumnName = keyColumn.getColumnName();
		}

		this.deleteSQL = "DELETE FROM " + remoteTableName + " WHERE " + remoteKeyColumnName + "=?";

		if(preserveListOrder){
			
			this.selectSQL = "SELECT * FROM " + remoteTableName + " WHERE " + remoteKeyColumnName + " = ? ORDER BY " + indexColumnName + " " + order;
			this.insertSQL = "INSERT INTO " + remoteTableName + "(" + remoteKeyColumnName + "," + remoteValueColumnName + "," + indexColumnName + ") VALUES (?,?,?)";
			
		}else{
			
			this.selectSQL = "SELECT * FROM " + remoteTableName + " WHERE " + remoteKeyColumnName + " = ? ORDER BY " + remoteValueColumnName + " " + order;
			this.insertSQL = "INSERT INTO " + remoteTableName + "(" + remoteKeyColumnName + "," + remoteValueColumnName + ") VALUES (?,?)";
		}
		
		this.initialized = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.unlogic.utils.dao.OneToManyRelation#setValue(LocalType, java.sql.Connection, java.lang.reflect.Field[])
	 */
	public void getRemoteValue(LocalType bean, Connection connection, RelationQuery relationQuery) throws SQLException {

		if (!initialized) {
			init();
		}

		try {
			ArrayListQuery<RemoteType> query = new ArrayListQuery<RemoteType>(connection, false, selectSQL, beanResultSetPopulator);

			setKey(query, bean);

			ArrayList<RemoteType> list = query.executeQuery();

			if (list != null) {

				CollectionUtils.removeNullValues(list);
			}

			field.set(bean, list);

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}

	private void setKey(PreparedStatementQuery query, LocalType bean) throws SQLException {

		if (keyColumn.getQueryParameterPopulator() != null) {

			keyColumn.getQueryParameterPopulator().populate(query, 1, bean);

		} else {

			try {
				keyColumn.getQueryMethod().invoke(query, 1, keyColumn.getBeanValue(bean));

			} catch (IllegalArgumentException e) {

				throw new RuntimeException(e);

			} catch (IllegalAccessException e) {

				throw new RuntimeException(e);

			} catch (InvocationTargetException e) {

				throw new RuntimeException(e);
			}
		}
	}

	private void setValue(RemoteType value, UpdateQuery query) throws SQLException {

		if (queryParameterPopulator != null) {

			queryParameterPopulator.populate(query, 2, value);

		} else {

			try {
				preparedStatementMethod.invoke(query, 2, value);

			} catch (IllegalArgumentException e) {

				throw new RuntimeException(e);

			} catch (IllegalAccessException e) {

				throw new RuntimeException(e);

			} catch (InvocationTargetException e) {

				throw new RuntimeException(e);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.unlogic.utils.dao.OneToManyRelation#add(LocalType, java.sql.Connection, java.lang.reflect.Field[])
	 */
	@SuppressWarnings("unchecked")
	public void add(LocalType bean, Connection connection, RelationQuery relationQuery) throws SQLException {

		if (!initialized) {
			init();
		}

		try {
			List<RemoteType> values = (List<RemoteType>) field.get(bean);

			if (values != null) {

				int listIndex = 0;
				
				for (RemoteType value : values) {

					UpdateQuery query = new UpdateQuery(connection, false, insertSQL);

					setKey(query, bean);

					setValue(value, query);

					if(preserveListOrder){
						
						query.setInt(3, listIndex);
						listIndex++;
					}
					
					query.executeUpdate();
				}
			}

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.unlogic.utils.dao.OneToManyRelation#update(LocalType, java.sql.Connection, java.lang.reflect.Field[])
	 */
	public void update(LocalType bean, Connection connection, RelationQuery relationQuery) throws SQLException {

		if (!initialized) {
			init();
		}

		UpdateQuery query = new UpdateQuery(connection, false, deleteSQL);

		setKey(query, bean);

		query.executeUpdate();

		this.add(bean, connection, relationQuery);
	}

	public static <LT, RT> OneToManyRelation<LT, RT> getGenericInstance(Class<LT> beanClass, Class<RT> remoteClass, Field field, AnnotatedDAO<LT> localDAO, List<? extends BeanStringPopulator<?>> typePopulators, List<? extends QueryParameterPopulator<?>> queryParameterPopulators) {

		return new SimplifiedOneToManyRelation<LT, RT>(beanClass, remoteClass, field, localDAO, typePopulators, queryParameterPopulators);
	}
}
