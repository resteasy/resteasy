package org.jboss.resteasy.plugins.server.netty.i18n;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.MessageLogger;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 *          Copyright Sep 1, 2015
 *
 * @deprecated use the new dependencies
 */
@Deprecated(forRemoval = true, since = "6.2.13.Final")
@MessageLogger(projectCode = "RESTEASY")
public interface LogMessages extends BasicLogger {
    LogMessages LOGGER = Logger.getMessageLogger(LogMessages.class, LogMessages.class.getPackage().getName());
}
