package org.jboss.resteasy.plugins.server.netty.i18n;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * 
 * Copyright Oct 10, 2014
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 8500;

   @Message(id = BASE + 0, value = "Already committed")
   String alreadyCommitted();

   @Message(id = BASE + 5, value = "Failed to parse request.")
   String failedToParseRequest();
   
   @Message(id = BASE + 10, value = "Request media type is not application/x-www-form-urlencoded")
   String requestMediaType();
   
   @Message(id = BASE + 15, value = "Unexpected")
   String unexpected();
}
