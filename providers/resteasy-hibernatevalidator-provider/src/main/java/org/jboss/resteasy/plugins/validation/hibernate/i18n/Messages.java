package org.jboss.resteasy.plugins.validation.hibernate.i18n;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.jboss.resteasy.api.validation.ConstraintType;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 25, 2015
 * 
 * @deprecated Use resteasy-validator-provider-11.
 */
@Deprecated
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 8000;
   
   @Message(id = BASE + 0, value = "ResteasyCdiExtension is not on the classpath. Assuming CDI is not active")
   String cdiExtensionNotOnClasspath();
   
   @Message(id = BASE + 5, value = "ResteasyViolationException has invalid format: %s")
   String exceptionHasInvalidFormat(String line);
   
   @Message(id = BASE + 10, value = "Unable to load Validation support")
   String unableToLoadValidationSupport();
   
   @Message(id = BASE + 15, value = "Unable to parse ResteasyViolationException")
   String unableToParseException();
    
   @Message(id = BASE + 20, value = "unexpected violation type: %s")
   String unexpectedViolationType(ConstraintType.Type type);
   
   @Message(id = BASE + 25, value = "unknown object passed as constraint violation: %s")
   String unknownObjectPassedAsConstraintViolation(Object o);
}
