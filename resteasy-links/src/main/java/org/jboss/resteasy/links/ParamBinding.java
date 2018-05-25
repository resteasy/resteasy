package org.jboss.resteasy.links;

/**
 * Defines a single URI template parameter binding. 
 * 
 * @author <a href="mailto:alexander.rashed@gmail.com">Alexander Rashed</a>
 */
public @interface ParamBinding {

	/**
	 * Name of the URI template parameter.
	 */
	String name();

	/**
	 * <p>
	 * Expression language expression specifying the value of the parameter.
	 * These can be normal constant string values or EL expressions (${foo.bar})
	 * that will resolve using either the default EL context, or the context
	 * specified by a {@link LinkELProvider @LinkELProvider} annotation. The
	 * default context resolves any non-qualified variable to properties of the
	 * response entity for whom we're discovering links, and has an extra
	 * binding for the "this" variable which is the response entity as well.
	 * </p>
	 */
	String value();
	
}
