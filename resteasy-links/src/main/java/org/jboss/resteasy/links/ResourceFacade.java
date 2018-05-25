package org.jboss.resteasy.links;

import java.util.Map;

/**
 * Represents a facade for an entity which should still receive links for this entity even
 * though this entity's instance is not available. This is useful for Collections of an entity's
 * children (Order.comments) where the parent entity (Order) is not returned as part of the collection,
 * but should still receive any links for the entity of type T (Comment) for whom we do not have any
 * instance (links for "add" and "list" for example).
 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
 *
 * @param <T> the type of entity that this facade should receive links for
 */
public interface ResourceFacade<T> {
	/**
	 * Returns the type of entity that this facade should receive links for. If we represent
	 * a list of Comment entities, then that would be the type to return.
	 */
	Class<T> facadeFor();
	
	/**
	 * <p>
	 * Returns a map of path parameters to use to build any path to this facade's type's links. If there
	 * are too many path parameter for a link, the extra ones will be ignored, but if there are not enough
	 * path parameters the link will be skipped.
	 * </p>
	 * <p>
	 * For example, when scanning a facade for a list of order comments for an order with ID "foo", 
	 * you should return a map for 
	 * {orderId {@literal =>} "foo"}, which will get you links for "/order/{orderId}/comments" ("list" and "add")
	 * but not for "/order/{orderId}/comment/{commentId}" since those need actual Comment instances ("self" and 
	 * "delete").
	 * </p>
	 */
	Map<String, ? extends Object> pathParameters();
}
