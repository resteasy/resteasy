package org.jboss.resteasy.plugins.server.sun.http.i18n;

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
   int BASE = 17500;

   @Message(id = BASE + 0, value = "[Embedded Container Start]")
   String embeddedContainerStart();

   @Message(id = BASE + 5, value = "[Embedded Container Stop]")
   String embeddedContainerStop();

   @Message(id = BASE + 10, value = "Error parsing request")
   String errorParsingRequest();
   
   @Message(id = BASE + 15, value = "WTF!")
   String wtf();
}
