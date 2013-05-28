package org.jboss.resteasy.spi.validation;

import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.metadata.BeanDescriptor;

import org.jboss.resteasy.validation.ResteasyConstraintViolation;

public interface GeneralValidator {

	public abstract <T> Set<ResteasyConstraintViolation> validate(T object,
			Class<?>... groups);

	public abstract <T> Set<ResteasyConstraintViolation> validateProperty(T object,
			String propertyName, Class<?>... groups);

	public abstract <T> Set<ResteasyConstraintViolation> validateValue(
			Class<T> beanType, String propertyName, Object value,
			Class<?>... groups);

	public abstract BeanDescriptor getConstraintsForClass(Class<?> clazz);

	public abstract <T> T unwrap(Class<T> type);

	public abstract <T> Set<ResteasyConstraintViolation> validateParameter(
			T object, Method method, Object parameterValue, int parameterIndex,
			Class<?>... groups);

	public abstract <T> Set<ResteasyConstraintViolation> validateAllParameters(
			T object, Method method, Object[] parameterValues,
			Class<?>... groups);

	public abstract <T> Set<ResteasyConstraintViolation> validateReturnValue(
			T object, Method method, Object returnValue, Class<?>... groups);

//	public abstract TypeDescriptor getConstraintsForType(Class<?> clazz);

}