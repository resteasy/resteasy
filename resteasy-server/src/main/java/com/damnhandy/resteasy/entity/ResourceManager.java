/**
 * 
 */
package com.damnhandy.resteasy.entity;

import java.io.Serializable;
import java.util.Map;

import com.damnhandy.resteasy.annotations.HttpMethod;
import com.damnhandy.resteasy.annotations.HttpMethods;
import com.damnhandy.resteasy.annotations.RepresentationIn;
import com.damnhandy.resteasy.representation.Representation;

/**
 * An interface which assists in managing the state of a RESTful Entity Bean
 * 
 * @author Ryan J. McDonough
 * @since 1.0
 * 
 */
public interface ResourceManager<T extends Serializable, V extends Serializable> {

	public static final String BASE_URI = "baseURI";
	public static final String INSTANCE_URI = "instanceURI";
	/**
	 * Retrieves the entity by 
	 * @param id
	 * @return
	 */
	@HttpMethod(resourceId=INSTANCE_URI,value=HttpMethod.GET)
	public Representation<T> getEntityById(V id);
	
	/**
	 * Removes the Entity 
	 * @param id
	 */
	@HttpMethods(
		methods={
			@HttpMethod(resourceId=INSTANCE_URI,value=HttpMethod.DELETE),
			@HttpMethod(resourceId=INSTANCE_URI,value=HttpMethod.POST,discriminator="delete")
		}
	)
	public void remove(V id);
	
	
	/**
	 * Persists the new state of the entity
	 * @param id
	 * @param entity
	 * @return
	 */
	@HttpMethods(
		methods={
			@HttpMethod(resourceId=INSTANCE_URI,value=HttpMethod.POST),
			@HttpMethod(resourceId=INSTANCE_URI,value=HttpMethod.POST,discriminator="update")
		}
	)
	public Representation<T> updateEntity(V id, T entity);
	
	/**
	 * Creates a new at the following URI. If the 
	 * @param entity
	 * @return
	 */
	@HttpMethods(
		methods={
			@HttpMethod(resourceId=BASE_URI,value=HttpMethod.PUT),
			@HttpMethod(resourceId=BASE_URI,value=HttpMethod.POST,discriminator="create")			
		}
	)
	public Representation<T> createEntity(T entity);
	
	/**
	 * Locates an entity by search for one of many properties
	 * @param args
	 * @return
	 */
	@HttpMethods(
		methods={
			@HttpMethod(resourceId=BASE_URI,value=HttpMethod.POST),
			@HttpMethod(resourceId=BASE_URI,value=HttpMethod.GET)			
		}
	)
	public Representation<T> findEntityByProperty(@RepresentationIn("application/x-www-form-urlencoded")
												 Map<String,String> queryParameters);
	
	
	
}
