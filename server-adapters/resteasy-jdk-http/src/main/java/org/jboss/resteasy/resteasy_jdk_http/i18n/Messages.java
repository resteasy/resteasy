package org.jboss.resteasy.resteasy_jdk_http.i18n;

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
   int BASE = 8000;

   @Message(id = BASE + 0, value = "Error parsing request")
   String errorParsingRequest();

   @Message(id = BASE + 5, value = "WTF!")
   String wtf();
}
