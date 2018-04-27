package org.jboss.resteasy.rxjava2.i18n;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 20500;
   
   @Message(id = BASE + 0, value = "Expected ClientInvocationBuilder, not: %s")
   String expectedClientInvocationBuilder(String className);
}
