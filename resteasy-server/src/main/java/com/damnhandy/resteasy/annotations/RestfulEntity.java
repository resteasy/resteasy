/**
 * 
 */
package com.damnhandy.resteasy.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ryan J. McDonough
 * Mar 3, 2007
 *
 *@RestfulEntity(baseURI="/contacts",
                 persistenceStrategy=PersistenceStrategy.JAXB_HIBERNATE,
                 excludeSearchProperties={"id","isActive"},
                 ignoreCase=true)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RestfulEntity {
	
	/**
	 * 
	 * @return
	 */
	public String baseURI();
	
	/**
	 * If the entity is read-only, update and delete operations will
	 * not be available.
	 * @return
	 */
	public boolean readOnly() default false;
	
	
	/**
	 * 
	 * @return
	 */
	public String[] excludeSearchProperties() default {};
	
	/**
	 * 
	 * @return
	 */
	public boolean ignoreCase();

}
