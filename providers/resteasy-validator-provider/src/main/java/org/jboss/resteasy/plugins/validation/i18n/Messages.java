package org.jboss.resteasy.plugins.validation.i18n;

import java.lang.invoke.MethodHandles;

import jakarta.validation.ValidatorFactory;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 *          Copyright Aug 25, 2015
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages {
    Messages MESSAGES = org.jboss.logging.Messages.getBundle(MethodHandles.lookup(), Messages.class);

    //   @Message(id = 8500, value = "ResteasyViolationException has invalid format: %s")
    //   String exceptionHasInvalidFormat(String line);

    @Message(id = 8505, value = "Expect two non-null methods")
    String expectTwoNonNullMethods();

    // @Message(id = 8510, value = "ResteasyCdiExtension is on the classpath.")
    // String resteasyCdiExtensionOnClasspath();

    // @Message(id = 8515, value = "ResteasyCdiExtension is not on the classpath. Assuming CDI is not active")
    // String resteasyCdiExtensionNotOnClasspath();

    @Message(id = 8520, value = "Unable to load Validation support")
    String unableToLoadValidationSupport();

    //   @Message(id = 8525, value = "Unable to parse ResteasyViolationException")
    //   String unableToParseException();

    //   @Message(id = 8530, value = "unexpected path node type: %s")
    //   String unexpectedPathNode(ElementKind kind);

    //   @Message(id = 8535, value = "unexpected path node type in method violation: %s")
    //   String unexpectedPathNodeViolation(ElementKind kind);

    //   @Message(id = 8540, value = "unexpected violation type: %s")
    //   String unexpectedViolationType(ConstraintType.Type type);

    //   @Message(id = 8545, value = "unknown object passed as constraint violation: %s")
    //   String unknownObjectPassedAsConstraintViolation(Object o);

    @Message(id = 8550, value = "Unable to find CDI supporting ValidatorFactory. Using default ValidatorFactory")
    String usingValidatorFactoryDoesNotSupportCDI();

    @Message(id = 8555, value = "Using CDI supporting %s")
    String usingValidatorFactorySupportsCDI(ValidatorFactory factory);

    @Message(id = 8560, value = "@ValidateOnExecution found on multiple overridden methods")
    String validateOnExceptionOnMultipleMethod();
}
