package org.jboss.resteasy.plugins.validation.hibernate;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.hibernate.validator.method.MethodConstraintViolation;
import org.hibernate.validator.method.MethodConstraintViolationException;
import org.hibernate.validator.method.MethodValidator;
import org.jboss.resteasy.spi.validation.DoNotValidateRequest;
import org.jboss.resteasy.spi.validation.ValidateRequest;
import org.jboss.resteasy.spi.validation.ValidatorAdapter;
import org.jboss.resteasy.util.FindAnnotation;

class HibernateValidatorAdapter implements ValidatorAdapter {

	private final Validator validator;
	private final MethodValidator methodValidator;

	HibernateValidatorAdapter(Validator validator) {
		if( validator == null )
			throw new IllegalArgumentException("Validator cannot be null");
		
		this.validator = validator;
		this.methodValidator = validator.unwrap(MethodValidator.class);
	}

	@Override
	public void applyValidation(Object resource, Method invokedMethod,
			Object[] args) {
		
		ValidateRequest resourceValidateRequest = FindAnnotation.findAnnotation(invokedMethod.getDeclaringClass().getAnnotations(), ValidateRequest.class);
		
		if( resourceValidateRequest != null ) {
			Set<ConstraintViolation<?>> constraintViolations = new HashSet<ConstraintViolation<?>>( validator.validate(resource, resourceValidateRequest.groups()) );
			
			if( constraintViolations.size() > 0 )
				throw new ConstraintViolationException(constraintViolations);
		}
		
		ValidateRequest methodValidateRequest = FindAnnotation.findAnnotation(invokedMethod.getAnnotations(), ValidateRequest.class);
		DoNotValidateRequest doNotValidateRequest = FindAnnotation.findAnnotation(invokedMethod.getAnnotations(), DoNotValidateRequest.class);
		
		if( (resourceValidateRequest != null || methodValidateRequest != null) && doNotValidateRequest == null ) {
			Set<Class<?>> set = new HashSet<Class<?>>();
			if( resourceValidateRequest != null ) {
				for (Class<?> group : resourceValidateRequest.groups()) {
					set.add(group);
				}
			}
			
			if( methodValidateRequest != null ) {
				for (Class<?> group : methodValidateRequest.groups()) {
					set.add(group);
				}
			}
			
			Set<MethodConstraintViolation<?>> constraintViolations = new HashSet<MethodConstraintViolation<?>>(methodValidator.validateAllParameters(resource, invokedMethod, args, set.toArray(new Class<?>[set.size()])));
			
			if(constraintViolations.size() > 0)
				throw new MethodConstraintViolationException(constraintViolations);
		}
	}
	
}
