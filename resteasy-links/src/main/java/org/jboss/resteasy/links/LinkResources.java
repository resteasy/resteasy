package org.jboss.resteasy.links;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows you have a list of {@link LinkResource} on a single method. This is useful if you want to add
 * the link for various entity types.
 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LinkResources {
	/**
	 * The list of {@link LinkResource} that apply on this method
	 */
	LinkResource[] value();
}
