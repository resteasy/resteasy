package org.jboss.resteasy.utils.maven;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.aether.impl.DefaultServiceLocator;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by mkopecky on 11/9/15.
 */
class ConsoleErrorHandler extends DefaultServiceLocator.ErrorHandler {

    protected static final Logger logger = LogManager.getLogger(ConsoleErrorHandler.class.getName());

    @Override
    public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
        logger.debug(String.format("Could not create type: %s, impl: %s", type, impl));

        StringWriter errors = new StringWriter();
        exception.printStackTrace(new PrintWriter(errors));
        logger.debug(errors.toString());
    }
}
