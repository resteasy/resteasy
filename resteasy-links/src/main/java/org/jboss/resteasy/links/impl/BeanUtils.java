package org.jboss.resteasy.links.impl;

import org.jboss.resteasy.links.ParentResource;
import org.jboss.resteasy.links.ResourceID;
import org.jboss.resteasy.links.ResourceIDs;
import org.jboss.resteasy.links.i18n.Messages;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlID;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BeanUtils {
	@SuppressWarnings(value = "unchecked")
	private static final Class<Annotation>[] IdAnnotationList = new Class[]{ResourceID.class, XmlID.class, Id.class};

	public static List<Object> findIDs(Object entity){
		Class<? extends Object> klass = entity.getClass();
		ResourceIDs resourceIDs = findTypeAnnotation(klass, ResourceIDs.class);
		if(resourceIDs != null){
			// return those properties
			String[] names = resourceIDs.value();
			List<Object> values = new ArrayList<Object>();
			for (String name : names) {
				try {
					values.add(getPropertyValue(entity, entity.getClass(), name));
				} catch (NotFoundException e) {
				   throw new RuntimeException(Messages.MESSAGES.failedToFindBeanProperty(name));
				}
			}
			return values;
		}
		for(Class<Annotation> idAnnotationClass : IdAnnotationList){
			try {
				return Collections.singletonList(findAnnotatedProperty(entity, klass, idAnnotationClass));
			} catch (NotFoundException e) {
				// ignore
			}
		}
		// we got nothing
		return Collections.emptyList();
	}

	private static Object getPropertyValue(Object entity,
			Class<?> klass, String name) throws NotFoundException {
		// easiest is a public property:
		try {
			return readPropertyMethods(entity, klass, name, false);
		} catch (NotFoundException e) {
			// ignore
		}
		// not found, try private properties
		do{
			try {
				return readPropertyMethods(entity, klass, name, true);
			} catch (NotFoundException e) {
				// ignore
			}
			// try the field
			try {
				Field f = klass.getDeclaredField(name);
				return readField(f, entity);
			} catch (SecurityException e) {
				// there's one but it's not accessible?
			   throw new RuntimeException(Messages.MESSAGES.failedToReadProperty(name), e);
			} catch (NoSuchFieldException e) {
				// ignore
			}
			// go up
			klass = klass.getSuperclass();
		}while(klass != null);
		// we got nothing
		throw new NotFoundException();
	}

	private static Object readPropertyMethods(Object entity, Class<?> klass,
			String propertyName, boolean b) throws NotFoundException {
		try {
			return readPropertyMethod(entity, klass, "is"+capitalise(propertyName), false);
		} catch (NotFoundException e) {
			// ignore
		}
		// let this one throw
		return readPropertyMethod(entity, klass, "get"+capitalise(propertyName), false);
	}

	private static Object readPropertyMethod(Object entity, Class<?> klass,
			String methodName, boolean declared) throws NotFoundException {
		try{
			Method getter = declared ? klass.getDeclaredMethod(methodName) : klass.getMethod(methodName);
			return readMethod(getter, entity);
		} catch (SecurityException e) {
			// there's one but it's not accessible?
		   throw new RuntimeException(Messages.MESSAGES.failedToReadProperty(methodName), e);
		} catch (NoSuchMethodException e) {
			throw new NotFoundException();
		}
	}

	private static String capitalise(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	private static <T extends Annotation> T findTypeAnnotation(Class<?> klass,
			Class<T> annotationClass) {
		do{
			if(klass.isAnnotationPresent(annotationClass))
				return klass.getAnnotation(annotationClass);
			klass = klass.getSuperclass();
		}while(klass != null);
		return null;
	}

	public static Object findParentResource(Object entity) {
		try {
			return findAnnotatedProperty(entity, entity.getClass(), ParentResource.class);
		} catch (NotFoundException e) {
			return null;
		}
	}

	private static Object findAnnotatedProperty(Object entity, Class<?> type, Class<? extends Annotation> annotation) throws NotFoundException {
		for(Field f : type.getDeclaredFields()){
			if(f.isAnnotationPresent(annotation)){
				return readField(f, entity);
			}
		}
		for(Method m : type.getDeclaredMethods()){
			if(m.isAnnotationPresent(annotation) && isBeanAccessor(m)){
				return readMethod(m, entity);
			}
		}
		if(type.getSuperclass() != null)
			return findAnnotatedProperty(entity, type.getSuperclass(), annotation);
		throw new NotFoundException();
	}

	private static Object readMethod(Method m, Object entity) {
		// read that property
		m.setAccessible(true);
		try {
			return m.invoke(entity);
		} catch (Exception e) {
		   throw new RuntimeException(Messages.MESSAGES.failedToReadPropertyFromMethod(m.getName()), e);
		}finally{
			m.setAccessible(false);
		}
	}

	private static Object readField(Field f, Object entity) {
		// read that field
		f.setAccessible(true);
		try {
			return f.get(entity);
		} catch (Exception e) {
		   throw new RuntimeException(Messages.MESSAGES.failedToReadField(f.getName()), e);
		}finally{
			f.setAccessible(false);
		}
	}

	private static boolean isBeanAccessor(Method m) {
		String name = m.getName();
		return (name.startsWith("get") || name.startsWith("is")) && m.getParameterTypes().length == 0;
	}
}
