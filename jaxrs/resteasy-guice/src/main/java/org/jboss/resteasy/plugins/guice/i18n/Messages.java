package org.jboss.resteasy.plugins.guice.i18n;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.jboss.logging.annotations.Message.Format;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 27, 2015
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 11000;

   @Message(id = BASE + 0, value = "Cannot execute expected module {0}''s @{1} method {2} because it has unexpected parameters: skipping.", format=Format.MESSAGE_FORMAT)
   String cannotExecute(String className, String annotation, String methodName);
   
   @Message(id = BASE + 05, value = "found module: %s")
   String foundModule(String module);
   
   @Message(id = BASE + 10, value = "Injector stage is not defined properly. %s is wrong value. Possible values are PRODUCTION, DEVELOPMENT, TOOL.")
   String injectorStageNotProperlyDefined(String stage);
   
   @Message(id = BASE + 15, value = "Problem running annotation method @%s")
   String problemRunningAnnotationMethod(String annotation);
   
   @Message(id = BASE + 20, value = "registering factory for %s")
   String registeringFactory(String className);
   
   @Message(id = BASE + 25, value = "registering provider instance for %s")
   String registeringProviderInstance(String className);
}
