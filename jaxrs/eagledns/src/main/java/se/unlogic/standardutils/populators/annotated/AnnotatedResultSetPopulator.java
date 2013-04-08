/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.populators.annotated;

import se.unlogic.standardutils.annotations.NoAnnotatedFieldsFoundException;
import se.unlogic.standardutils.annotations.UnsupportedFieldTypeException;
import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.dao.ResultSetField;
import se.unlogic.standardutils.dao.ResultSetMethods;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.ManyToMany;
import se.unlogic.standardutils.dao.annotations.ManyToOne;
import se.unlogic.standardutils.dao.annotations.OneToMany;
import se.unlogic.standardutils.dao.annotations.OneToOne;
import se.unlogic.standardutils.populators.BeanStringPopulator;
import se.unlogic.standardutils.populators.BeanStringPopulatorRegistery;
import se.unlogic.standardutils.populators.EnumPopulator;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.string.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class AnnotatedResultSetPopulator<T> implements BeanResultSetPopulator<T>{

	protected Class<T> beanClass;
	protected HashMap<Field,ResultSetField> resultSetFieldMap = new HashMap<Field,ResultSetField>();

	public AnnotatedResultSetPopulator(Class<T> beanClass) throws UnsupportedFieldTypeException{
		this(beanClass, (List<BeanStringPopulator<?>>)null);
	}

	public AnnotatedResultSetPopulator(Class<T> beanClass, BeanStringPopulator<?>... populators) throws UnsupportedFieldTypeException{
		this(beanClass,Arrays.asList(populators));
	}

	@SuppressWarnings("unchecked")
	public AnnotatedResultSetPopulator(Class<T> beanClass, List<? extends BeanStringPopulator<?>> populators) throws UnsupportedFieldTypeException{

		this.beanClass = beanClass;

		//cache fields
		List<Field> fields = ReflectionUtils.getFields(beanClass);

		for(Field field : fields){

			DAOManaged annotation = field.getAnnotation(DAOManaged.class);

			if(annotation != null && (!field.isAnnotationPresent(OneToOne.class) && !field.isAnnotationPresent(OneToMany.class) && !field.isAnnotationPresent(ManyToOne.class) && !field.isAnnotationPresent(ManyToMany.class) )){

				if(Modifier.isFinal(field.getModifiers())){

					throw new UnsupportedFieldTypeException("The annotated field " + field.getName() + " in class " + beanClass + " is final!", field, annotation.getClass(), beanClass);

				}

				Method resultSetColumnNameMethod = ResultSetMethods.getColumnNameMethod(field.getType());

				BeanStringPopulator<?> typePopulator = null;

				if(resultSetColumnNameMethod == null){

					if(populators != null){
						typePopulator = this.getPopulator(populators, field, annotation);
					}
					
					if(typePopulator == null){
						
						typePopulator = BeanStringPopulatorRegistery.getBeanStringPopulator(field.getType());
					}
					
					if(typePopulator == null){
						
						if(field.getType().isEnum()){

							typePopulator = EnumPopulator.getInstanceFromField(field);

						}else if(List.class.isAssignableFrom(field.getType()) && ReflectionUtils.getGenericlyTypeCount(field) == 1 && ((Class<?>)ReflectionUtils.getGenericType(field)).isEnum()){

							typePopulator = EnumPopulator.getInstanceFromListField(field);
						}	
					}

					if(typePopulator == null){
						throw new UnsupportedFieldTypeException("The annotated field " + field.getName() + " in class " + beanClass + " is of unsupported type " + field.getType(), field, annotation.annotationType() , beanClass);
					}
				}

				ReflectionUtils.fixFieldAccess(field);

				Method resultSetColumnIndexMethod = ResultSetMethods.getColumnIndexMethod(field.getType());

				if(!StringUtils.isEmpty(annotation.columnName())){

					this.resultSetFieldMap.put(field,new ResultSetField(field,resultSetColumnNameMethod,resultSetColumnIndexMethod,annotation.columnName(),typePopulator));

				}else{

					this.resultSetFieldMap.put(field,new ResultSetField(field,resultSetColumnNameMethod,resultSetColumnIndexMethod,field.getName(),typePopulator));
				}
			}
		}

		if(this.resultSetFieldMap.isEmpty()){
			throw new NoAnnotatedFieldsFoundException(beanClass,DAOManaged.class);
		}
	}

	private BeanStringPopulator<?> getPopulator(List<? extends BeanStringPopulator<?>> populators, Field field, DAOManaged annotation) {

		String populatorID = annotation.populatorID();

		Object clazz = field.getType();

		for(BeanStringPopulator<?> populator : populators){

			if(clazz.equals(populator.getType())){

				if((StringUtils.isEmpty(populatorID) && populator.getPopulatorID() == null) || populatorID.equals(populator.getPopulatorID())){

					return populator;
				}
			}
		}

		return null;
	}

	public T populate(ResultSet rs) throws SQLException, BeanResultSetPopulationException {

		ResultSetField currentField = null;

		try {
			T bean = beanClass.newInstance();

			for(ResultSetField resultSetField : this.resultSetFieldMap.values()){

				currentField = resultSetField;

				if(currentField.getResultSetColumnNameMethod() != null){

					Object value = resultSetField.getResultSetColumnNameMethod().invoke(rs, resultSetField.getAlias());

					if(rs.wasNull() && !resultSetField.getBeanField().getType().isPrimitive()){

						resultSetField.getBeanField().set(bean, null);

					}else{

						resultSetField.getBeanField().set(bean, value);
					}

				}else{

					String value = rs.getString(currentField.getAlias());

					if(value != null || currentField.getBeanStringPopulator().getType().isPrimitive()){

						resultSetField.getBeanField().set(bean, currentField.getBeanStringPopulator().getValue(value));
					}else{
						resultSetField.getBeanField().set(bean, null);
					}
				}
			}

			return bean;

		} catch (InstantiationException e) {

			throw new BeanResultSetPopulationException(currentField,e);

		} catch (IllegalAccessException e) {

			throw new BeanResultSetPopulationException(currentField,e);

		} catch (IllegalArgumentException e) {

			throw new BeanResultSetPopulationException(currentField,e);

		} catch (InvocationTargetException e) {

			throw new BeanResultSetPopulationException(currentField,e);
		}
	}

	public ResultSetField getResultSetField(Field field){

		return this.resultSetFieldMap.get(field);
	}
}
