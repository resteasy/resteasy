/**
 * 
 */
package com.damnhandy.resteasy.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.damnhandy.resteasy.entity.DefaultEJBResourceManagerBean;

/**
 * @author Ryan J. McDonough
 * @since 1.0
 * Mar 3, 2007
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RestfulEntity {
	
	/**
	 * Returns the baseURI for this entity. This is the URI that maintains the collection
	 * of entities of this type. This URI is also used to perform serach and creation 
	 * operations A valid value would be:
	 * <pre>
	 * /contacts
	 * </pre>
	 * 
	 * @return
	 */
	public String baseURI();
	
	
	/**
	 * <p>
	 * Returns the URI that will return a specific entity instace. And example would be:
	 * </p>
	 * <pre>
	 * /contacts/12345
	 * </pre>
	 * <p>
	 * The portion of the URI which represents an entity itentifier should be written using
	 * a URI template expression
	 * </p>
	 * <pre>
	 * /contacts/{contactId}
	 * </pre>
	 * <p>
	 * The contactId parameter will be mapped to a property mapped by an &#64;URIParam 
	 * annotation.
	 * </p>
	 * @return
	 */
	public String instanceURI();
	
	/**
	 * If the entity is read-only, update and delete operations will
	 * not be available.
	 * @return
	 */
	public boolean readOnly() default false;
	
	
	/**
	 * Returns an array of proprty names that should be excluded from any search.
	 * @return
	 */
	public String[] excludeSearchProperties() default {};
	
	
	/**
	 * Returns the ResourceManager used to manage this entity. If none is specified,
	 * the {@link DefaultEJBResourceManager} provided by RESTEasy is used. 
	 * @return 
	 */
	public Class resourceManager() default DefaultEJBResourceManagerBean.class;

}
