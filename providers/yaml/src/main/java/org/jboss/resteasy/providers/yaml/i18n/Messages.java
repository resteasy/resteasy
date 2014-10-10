package org.jboss.resteasy.providers.yaml.i18n;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * 
 * Copyright Oct 8, 2014
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 5500;

   @Message(id = BASE + 0, value = "Failed to decode Yaml")
   String failedToDecodeYaml();
   
   @Message(id = BASE + 5, value = "Failed to decode Yaml: %s")
   String failedToDecodeYamlMessage(String message);

   @Message(id = BASE + 10, value = "Failed to encode yaml for object: %s")
   String failedToEncodeYaml(String object);
}
