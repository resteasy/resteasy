package org.jboss.resteasy.links;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Marks this property as this resource's ID in URI templates.
 * </p>
 * <p>
 * Suppose this resource can be accessed using the URI template <tt>/orders/{name}</tt>,
 * and your resource holds the <tt>name</tt> bean property, then your
 * <tt>name</tt> property should be annotated with {@link ResourceID @ResourceID}.
 * </p>
 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResourceID {

}
