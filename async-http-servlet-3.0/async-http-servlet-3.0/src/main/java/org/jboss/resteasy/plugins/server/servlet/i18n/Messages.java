package org.jboss.resteasy.plugins.server.servlet.i18n;

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
   int BASE = 9500;
   
   @Message(id = BASE + 0, value = "-- already canceled")
   String alreadyCanceled();
   
   @Message(id = BASE + 5, value = "-- already done")
   String alreadyDone();
   
   @Message(id = BASE + 10, value = "Already suspended")
   String alreadySuspended();
   
   @Message(id = BASE + 15, value = "cancel()")
   String cancel();
   
   @Message(id = BASE + 20, value = "-- cancelling with 503")
   String cancellingWith503();
   
   @Message(id = BASE + 25, value = "onComplete")
   String onComplete();
   
   @Message(id = BASE + 30, value = "onTimeout")
   String onTimeout();
   
   @Message(id = BASE + 35, value = "Request not suspended")
   String requestNotSuspended();
   
   @Message(id = BASE + 40, value = "scheduled timeout")
   String scheduledTimeout();
   
   @Message(id = BASE + 45, value = "scheduling timeout")
   String schedulingTimeout();
}
