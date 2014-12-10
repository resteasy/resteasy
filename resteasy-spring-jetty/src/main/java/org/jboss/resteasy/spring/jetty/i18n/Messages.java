package org.jboss.resteasy.spring.jetty.i18n;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * 
 * Copyright Dec 08, 2014
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 11500;

   @Message(id = BASE + 0, value = "Exception while shutting down Jetty")
   String exceptionWhileShuttingDownJetty();

   @Message(id = BASE + 5, value = "Exception while starting up Jetty")
   String exceptionWhileStartingJetty();
   
   @Message(id = BASE + 10, value = "Interrupted while starting up Jetty")
   String interruptedWhileStartingJetty();
 
   @Message(id = BASE + 15, value = "Shutting down Jetty")
   String shuttingDownJetty();
   
   @Message(id = BASE + 20, value = "Starting up Jetty")
   String startingJetty();
}
