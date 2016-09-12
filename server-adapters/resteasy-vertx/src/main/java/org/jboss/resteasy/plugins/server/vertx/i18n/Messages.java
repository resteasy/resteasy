package org.jboss.resteasy.plugins.server.vertx.i18n;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Sep 1, 2015
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 19500;

   @Message(id = BASE + 0, value = "Already committed")
   String alreadyCommitted();
   
   @Message(id = BASE + 5, value = "Already suspended")
   String alreadySuspended();   
   
   @Message(id = BASE + 10, value = "Chunk size must be at least 1")
   String chunkSizeMustBeAtLeastOne();
   
   @Message(id = BASE + 20, value = "response is committed")
   String responseIsCommitted();
   
   @Message(id = BASE + 25, value = "Unexpected")
   String unexpected();
}
