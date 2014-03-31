package org.jboss.resteasy.plugins.validation;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.validation.GeneralValidatorCDI;

/**
 * 
 * @author Leandro Ferro Luzia
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 23, 2013
 */
@Provider
public class ValidatorContextResolverCDI extends AbstractValidatorContextResolver
implements ContextResolver<GeneralValidatorCDI>
{
}
