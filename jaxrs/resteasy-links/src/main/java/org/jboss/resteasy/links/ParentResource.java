package org.jboss.resteasy.links;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Marks an entity's parent which will be used to resolve any parent ID used in path parameters.
 * </p>
 * <p>
 * For example, if an entity needs its parent ID and its ID in the path of a resource method, we
 * will use this entity's {@link javax.xml.bind.annotation.XmlID @XmlID} id, and its parent's, in
 * reverse order, to form the path parameter list (from the furthest parent, to this entity).
 * </p>
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParentResource {
}
