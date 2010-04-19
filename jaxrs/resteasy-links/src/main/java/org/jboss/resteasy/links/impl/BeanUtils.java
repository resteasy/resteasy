package org.jboss.resteasy.links.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.xml.bind.annotation.XmlID;

import org.jboss.resteasy.links.ParentResource;

public class BeanUtils {
	
	public static Object findID(Object entity){
		return findAnnotatedProperty(entity, entity.getClass(), XmlID.class);
	}

	public static Object findParentResource(Object entity){
		return findAnnotatedProperty(entity, entity.getClass(), ParentResource.class);
	}

	private static Object findAnnotatedProperty(Object entity, Class<?> type, Class<? extends Annotation> annotation) {
		for(Field f : type.getDeclaredFields()){
			if(f.isAnnotationPresent(annotation)){
				// read that field
				f.setAccessible(true);
				try {
					return f.get(entity);
				} catch (Exception e) {
					throw new RuntimeException("Failed to read "+annotation.getName()+" from field "+f.getName(), e);
				}finally{
					f.setAccessible(false);
				}
			}
		}
		for(Method m : type.getDeclaredMethods()){
			if(m.isAnnotationPresent(annotation) && m.getName().startsWith("get") && m.getParameterTypes().length == 0){
				// read that property
				m.setAccessible(true);
				try {
					return m.invoke(entity);
				} catch (Exception e) {
					throw new RuntimeException("Failed to read "+annotation.getName()+" from method "+m.getName(), e);
				}finally{
					m.setAccessible(false);
				}
			}
		}
		if(type.getSuperclass() != null)
			return findAnnotatedProperty(entity, type.getSuperclass(), annotation);
		return null;
	}
}
