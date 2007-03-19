/**
 * 
 */
package com.damnhandy.resteasy.core;

import java.lang.reflect.Field;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Ryan J. McDonough
 * Feb 16, 2007
 *
 */
public class ETagGenerator {

	public static String generateETag(Object object) {
		if(object.getClass().isAnnotationPresent(Entity.class)) {
			
		}
		
		return object.toString();
	}
	
	public static String generateETagFromEntity(Object entity) 
		throws IllegalArgumentException, IllegalAccessException {
		Class<?> entityClass = entity.getClass();
		Field[] fields = entityClass.getDeclaredFields();
		for(int i = 0; i < fields.length; i++) {
			if(fields[i].isAnnotationPresent(Id.class)) {
				return fields[i].get(entity).toString();
			}
		}
		return null;
	}
	
}
