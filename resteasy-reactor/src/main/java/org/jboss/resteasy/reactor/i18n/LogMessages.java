package org.jboss.resteasy.reactor.i18n;

import java.lang.invoke.MethodHandles;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.MessageLogger;

@MessageLogger(projectCode = "RESTEASY")
public interface LogMessages extends BasicLogger {
    LogMessages LOGGER = Logger.getMessageLogger(MethodHandles.lookup(), LogMessages.class,
            LogMessages.class.getPackage().getName());
}
