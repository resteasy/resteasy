package org.jboss.resteasy.plugins.validation.hibernate;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.util.AnnotationLiteral;
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
			
			if( resourceValidateRequest != null ) {
				
				if (isSessionBean(invokedMethod.getDeclaringClass()) || isSessionBean(resource.getClass()))
				{
					if (!isWeldProxy(resource.getClass()))
					{
						Class<?>[] interfaces = getInterfaces(invokedMethod.getDeclaringClass());
						if (interfaces.length > 0)
						{
							resource = getProxy(resource.getClass(), interfaces, resource);
						}
					}
				}
				Set<ConstraintViolation<?>> constraintViolations = new HashSet<ConstraintViolation<?>>( validator.validate(resource, resourceValidateRequest.groups()) );
				
				if( constraintViolations.size() > 0 )
					throw new ConstraintViolationException(constraintViolations);
			}
			
			Set<MethodConstraintViolation<?>> constraintViolations = new HashSet<MethodConstraintViolation<?>>(methodValidator.validateAllParameters(resource, invokedMethod, args, set.toArray(new Class<?>[set.size()])));
			
			if(constraintViolations.size() > 0)
				throw new MethodConstraintViolationException(constraintViolations);
		}
	}
	
	public abstract static class S1 extends AnnotationLiteral<Stateless> implements Stateless { }
	public static final Annotation STATELESS = new S1() 
	{
		@Override public String name() {return null;}
		@Override public String mappedName() {return null;}
		@Override public String description() {return null;}
	};

	public abstract static class S2 extends AnnotationLiteral<Stateful> implements Stateful { }
	public static final Annotation STATEFUL = new S2() 
	{
		@Override public String name() {return null;}
		@Override public String mappedName() {return null;}
		@Override public String description() {return null;}
	};

	public abstract static class S3 extends AnnotationLiteral<Stateful> implements Stateful { }
	public static final Annotation SINGLETON = new S3() 
	{
		@Override public String name() {return null;}
		@Override public String mappedName() {return null;}
		@Override public String description() {return null;}
	};

	public abstract static class S4 extends AnnotationLiteral<Local> implements Local { }
	public static final Annotation LOCAL = new S4() 
	{
		@Override
		public Class<?>[] value() {return null;}
	};

	public abstract static class S5 extends AnnotationLiteral<Remote> implements Remote { }
	public static final Annotation REMOTE = new S5() 
	{
		@Override
		public Class<?>[] value() {return null;}
	};

	private static boolean isSessionBean(Class<?> clazz)
	{
		if (clazz.getName().indexOf("$$$view") >= 0)
		{
			return true;
		}
		while (clazz != null)
		{
			if (clazz.getAnnotation(STATELESS.annotationType()) != null 
					|| clazz.getAnnotation(STATEFUL.annotationType()) != null
					|| clazz.getAnnotation(SINGLETON.annotationType()) != null
					|| clazz.getAnnotation(LOCAL.annotationType()) != null
					|| clazz.getAnnotation(REMOTE.annotationType()) != null)
			{
				return true;
			}
			clazz = clazz.getSuperclass();
		}
		return false;
	}
	
	private static Object getProxy(Class<?> clazz, Class<?>[] interfaces, final Object delegate)
	{
		InvocationHandler handler = new InvocationHandler()
		{
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				return method.invoke(delegate, args);
			}
		};
		Object proxy = Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, handler);
		return proxy;
	}

	private static final String PROXY_OBJECT_INTERFACE_NAME = "javassist.util.proxy.ProxyObject";
	private static final String TARGET_INSTANCE_INTERFACE_NAME = "org.jboss.interceptor.util.proxy.TargetInstanceProxy";

	/**
	 * Whether the given class is a proxy created by Weld or not. This is
	 * the case if the given class implements the interface
	 * {@code org.jboss.weld.bean.proxy.ProxyObject}.
	 * 
	 * Borrowed from org.jboss.resteasy.spi.metadata.ResourceBuilder.
	 *
	 * @param clazz the class of interest
	 *
	 * @return {@code true} if the given class is a Weld proxy,
	 * {@code false} otherwise
	 */
	private static boolean isWeldProxy(Class<?> clazz)
	{
		boolean foundProxyObject = false;
		boolean foundTargetInstance = false;

		for ( Class<?> implementedInterface : clazz.getInterfaces() )
		{
			if ( implementedInterface.getName().equals( PROXY_OBJECT_INTERFACE_NAME ) )
			{
				foundProxyObject = true;
			}
			else if ( implementedInterface.getName().equals( TARGET_INSTANCE_INTERFACE_NAME ) )
			{
				foundTargetInstance = true;
			}
			if (foundProxyObject && foundTargetInstance)
			{
				return true;
			}
		}

		if (clazz.getName().contains("_$$_Weld"))
		{
			return true;
		}

		return false;
	}

	private static Class<?>[] getInterfaces(Class<?> clazz)
	{
		ArrayList<Class<?>> list = new ArrayList<Class<?>>();
		getInterfaces(list, clazz);
		return list.toArray(new Class<?>[] {});
	}

	private static void getInterfaces(ArrayList<Class<?>> list, Class<?> clazz)
	{
		Class<?>[] interfaces = clazz.getInterfaces();
		for (int i = 0; i < interfaces.length; i++)
		{
			list.add(interfaces[i]);
			getInterfaces(list, interfaces[i]);
		}
	}
}
