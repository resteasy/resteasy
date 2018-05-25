package org.jboss.resteasy.links;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use on your JAX-RS method, resource class or resource class' package to indicate that 
 * you have a custom ELProvider for any links added to this response's entity.
 * 
 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LinkELProvider {
	/**
	 * The class for the custom ELProvider that will set up the EL context for your
	 * resource discovery.
	 */
	Class<? extends ELProvider> value();
}
