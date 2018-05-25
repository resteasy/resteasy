package org.jboss.resteasy.links;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this to mark JAX-RS methods that should be included in the REST service discovery.
 * All parameters are optional and may be guessed from the method, whenever possible.
 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LinkResource {
	/**
	 * The type of entity that should receive links for this service. Defaults to the request's body
	 * entity type, or the response entity type if they are not a {@link java.util.Collection Collection} or a {@link javax.ws.rs.core.Response Response}.
	 */
	Class<?> value() default Void.class;

	/**
	 * The link relation. This defaults as follows:
	 * <dl>
	 *  <dt>list</dt>
	 *  <dd>{@link javax.ws.rs.GET GET} for {@link ResourceFacade} entities or methods that return a {@link java.util.Collection Collection}</dd>
	 * </dl>
	 * <dl>
	 *  <dt>self</dt>
	 *  <dd>{@link javax.ws.rs.GET GET} for non-{@link ResourceFacade} and non-{@link java.util.Collection Collection} entities</dd>
	 * </dl>
	 * <dl>
	 *  <dt>remove</dt>
	 *  <dd>{@link javax.ws.rs.DELETE DELETE}</dd>
	 * </dl>
	 * <dl>
	 *  <dt>add</dt>
	 *  <dd>{@link javax.ws.rs.POST POST}</dd>
	 * </dl>
	 * <dl>
	 *  <dt>update</dt>
	 *  <dd>{@link javax.ws.rs.PUT PUT}</dd>
	 * </dl>
	 */
	String rel() default "";

	/**
	 * <p>
	 * List of path parameter EL from the leftmost URI path parameter to the rightmost. These can be
	 * normal constant string values or EL expressions (${foo.bar}) that will resolve using either the 
	 * default EL context, or the context specified by a {@link LinkELProvider @LinkELProvider} annotation. The default context
	 * resolves any non-qualified variable to properties of the response entity for whom we're discovering
	 * links, and has an extra binding for the "this" variable which is the response entity as well.
	 * </p>
	 * <p>
	 * If there are too many parameters for the current path, only the leftmost useful ones will be used.
	 * </p>
	 * <p>
	 * Defaults to using discovery of values from the entity itself with {@link ResourceID @ResourceID},
	 * {@link ResourceIDs @ResourceIDs}, JAXB's @XmlID or JPA's @Id and {@link ParentResource @ParentResource}.
	 * </p>
	 * <p>
	 * This is not used for {@link ResourceFacade} entities, which provide their own list of path parameters.
	 * </p>
	 */
	String[] pathParameters() default {};
	
	/**
	 * <p>
	 * List of query parameters which should be attached to the link.
	 * </p>
	 */
	ParamBinding[] queryParameters() default {};
	
	/**
	 * <p>
	 * List of matrix parameters which should be attached to the link.
	 * </p>
	 */
	ParamBinding[] matrixParameters() default {};
	
	/**
	 * <p>
	 * EL expression that should return a boolean indicating whether or not this service link should be used.
	 * This is useful for security constraints limiting access to resources. Defaults to using the
	 * {@link javax.annotation.security.RolesAllowed @RolesAllowed} annotation using the current 
	 * {@link javax.ws.rs.core.SecurityContext SecurityContext} to check the current user's permissions.
	 * </p>
	 * <p>
	 * This can be a normal constant boolean value ("true" or "false") or an EL expression 
	 * (${foo.bar}) that will resolve using either the 
	 * default EL context, or the context specified by a {@link LinkELProvider @LinkELProvider} annotation. 
	 * </p>
	 * <p>
	 * For entities that are not {@link ResourceFacade}, the default context
	 * resolves any non-qualified variable to properties of the response entity for whom we're discovering
	 * links, and has an extra binding for the "this" variable which is the response entity as well.
	 * </p>
	 * <p>
	 * For entities that are a {@link ResourceFacade}, the default context
	 * has single binding for the "this" variable which is the {@link ResourceFacade}'s entity type 
	 * ({@link java.lang.Class Class} instance).
	 * </p>
	 */
	String constraint() default "";
}
