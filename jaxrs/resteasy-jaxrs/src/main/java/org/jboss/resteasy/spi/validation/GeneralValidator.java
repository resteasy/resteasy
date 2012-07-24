package org.jboss.resteasy.spi.validation;

import org.hibernate.validator.method.MethodValidator;

import javax.validation.Validator;

/** 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created Mar 7, 2012
 */
public interface GeneralValidator extends Validator, MethodValidator
{

}
