package org.jboss.resteasy.wadl;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.MessageLogger;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */

@MessageLogger(projectCode = "RESTEASY")
public interface ResteasyWadlLogMessages extends BasicLogger {
    ResteasyWadlLogMessages LOGGER = Logger.getMessageLogger(ResteasyWadlLogMessages.class, ResteasyWadlLogMessages.class.getPackage().getName());
}
