package org.jboss.resteasy.links;

import javax.el.ELContext;

/**
 * Implement this interface to wrap, modify or extend RESTEasy's ELContext with
 * your own variable or method resolvers.
 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
 */
public interface ELProvider {
	/**
	 * Returns an ELContext to use for any @LinkResource constraint and pathParameters.
	 * You can wrap the given default context or extend it, but you must return something.
	 * @param ctx the default content to wrap, extend, or just return as is.
	 * @return the ELContext to use for @LinkResource
	 */
	ELContext getContext(ELContext ctx);
}
