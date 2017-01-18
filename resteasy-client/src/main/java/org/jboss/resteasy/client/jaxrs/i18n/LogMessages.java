package org.jboss.resteasy.client.jaxrs.i18n;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.Message.Format;
import org.jboss.logging.annotations.MessageLogger;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 26, 2015
 */
@MessageLogger(projectCode = "RESTEASY")
public interface LogMessages extends BasicLogger
{
   LogMessages LOGGER = Logger.getMessageLogger(LogMessages.class, LogMessages.class.getPackage().getName());
   int BASE = 4800;
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 0, value = "Please consider updating the version of Apache HttpClient being used.  Version 4.3.6+ is recommended.", format=Format.MESSAGE_FORMAT)
   void updateOfApacheHttpClientNeeded();
}
