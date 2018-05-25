package org.jboss.resteasy.links;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Defines an ordered list of bean properties that defines this resource's ID in
 * URI templates.
 * </p>
 * <p>
 * Suppose this resource can be accessed using the URI template <tt>/orders/{name}/{id}</tt>,
 * and your resource holds the two bean properties <tt>name</tt> and <tt>id</tt>, then your
 * resource class should be annotated with {@link ResourceIDs @ResourceIDs({"name", "value"})}.
 * </p>
 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResourceIDs {
	String[] value();
}
