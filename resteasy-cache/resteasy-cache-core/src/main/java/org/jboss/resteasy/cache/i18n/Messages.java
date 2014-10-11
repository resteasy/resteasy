package org.jboss.resteasy.cache.i18n;

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
   int BASE = 7500;

   @Message(id = BASE + 0, value = "Resteasy is not intialized, could not find ResteasyProviderFactory attribute")
   String resteasyIsNotInitialized();
}
