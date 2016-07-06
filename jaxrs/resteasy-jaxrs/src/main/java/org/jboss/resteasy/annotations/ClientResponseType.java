package org.jboss.resteasy.annotations;

import org.jboss.resteasy.client.EntityTypeFactory;
import org.jboss.resteasy.client.core.VoidEntityTypeFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is an annotation that you can add to a RESTEasy client interface that
 * has a return type of Response.
 * <p/>
 * <p/>
 * You have two options:
 * <ol>
 * <li>use the entityType property to set a Class that will always be returned
 * <li>use the entityTypeFactory to determine which Class to use based on a
 * factory that determines which class to use based on logic that uses the
 * headers and status of the result.
 * </ol>
 * <p/>
 * Note: if you want to use generic types, you can't use this annotation. You'll
 * have to either use ClientResponse as part of your interface, or cast the
 * resulting Response object as a ClientResponse.
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 * 
 * @deprecated The Resteasy client framework in resteasy-jaxrs is replaced by the JAX-RS 2.0 compliant resteasy-client module.
 * 
 * @see javax.ws.rs.core.Response
 * @see jaxrs-api (https://jcp.org/en/jsr/detail?id=339)
 */
@Deprecated
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("unchecked")
public @interface ClientResponseType
{
   Class entityType() default Void.class;

   Class<? extends EntityTypeFactory> entityTypeFactory() default VoidEntityTypeFactory.class;
}
