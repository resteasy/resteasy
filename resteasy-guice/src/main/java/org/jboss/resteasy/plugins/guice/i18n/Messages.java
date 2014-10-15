package org.jboss.resteasy.plugins.guice.i18n;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * 
 * Copyright Oct 11, 2014
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 9000;

   @Message(id = BASE + 0, value = "found module: %s")
   String foundModule(String module);
   
   @Message(id = BASE + 5, value = "Injector stage is not defined properly. %s is wrong value. Possible values are PRODUCTION, DEVELOPMENT, TOOL")
   String injectorStageNotProperlyDefined(String stage);
   
   @Message(id = BASE + 10, value = "registering factory for %s")
   String registeringFactory(String className);
   
   @Message(id = BASE + 15, value = "registering provider instance for %s")
   String registeringProviderInstance(String className);
}
