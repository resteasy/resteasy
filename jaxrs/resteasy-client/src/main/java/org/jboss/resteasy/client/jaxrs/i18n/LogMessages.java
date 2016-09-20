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
   int BASE = 8000;

   LogMessages LOGGER = Logger.getMessageLogger(LogMessages.class, LogMessages.class.getPackage().getName());

   @LogMessage(level = Level.DEBUG)
   @Message(id = BASE + 0, value = "Call of interceptor : %s.%s , start of method call : %s .")
   void interceptorStartMethodCall(String interceptorPackage, String interceptorClass, String interceptorMethod);

   @LogMessage(level = Level.DEBUG)
   @Message(id = BASE + 5, value = "Call of interceptor : %s.%s , leave of method call : %s .")
   void interceptorLeaveMethodCall(String interceptorPackage, String interceptorClass, String interceptorMethod);
}
