package org.jboss.resteasy.plugins.validation.i18n;

import javax.validation.ValidatorFactory;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 25, 2015
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 8500;
   
//   @Message(id = BASE + 0, value = "ResteasyViolationException has invalid format: %s")
//   String exceptionHasInvalidFormat(String line);
   
   @Message(id = BASE + 05, value = "Expect two non-null methods")
   String expectTwoNonNullMethods();
   
   @Message(id = BASE + 10, value = "ResteasyCdiExtension is on the classpath.")
   String resteasyCdiExtensionOnClasspath();
   
   @Message(id = BASE + 15, value = "ResteasyCdiExtension is not on the classpath. Assuming CDI is not active")
   String resteasyCdiExtensionNotOnClasspath();
   
   @Message(id = BASE + 20, value = "Unable to load Validation support")
   String unableToLoadValidationSupport();
   
//   @Message(id = BASE + 25, value = "Unable to parse ResteasyViolationException")
//   String unableToParseException();
    
//   @Message(id = BASE + 30, value = "unexpected path node type: %s")
//   String unexpectedPathNode(ElementKind kind);
   
//   @Message(id = BASE + 35, value = "unexpected path node type in method violation: %s")
//   String unexpectedPathNodeViolation(ElementKind kind);
   
//   @Message(id = BASE + 40, value = "unexpected violation type: %s")
//   String unexpectedViolationType(ConstraintType.Type type);
   
//   @Message(id = BASE + 45, value = "unknown object passed as constraint violation: %s")
//   String unknownObjectPassedAsConstraintViolation(Object o);
   
   @Message(id = BASE + 50, value = "Unable to find CDI supporting ValidatorFactory. Using default ValidatorFactory")
   String usingValidatorFactoryDoesNotSupportCDI();
   
   @Message(id = BASE + 55, value = "Using CDI supporting %s")
   String usingValidatorFactorySupportsCDI(ValidatorFactory factory);
   
   @Message(id = BASE + 60, value = "@ValidateOnExecution found on multiple overridden methods")
   String validateOnExceptionOnMultipleMethod();
}
