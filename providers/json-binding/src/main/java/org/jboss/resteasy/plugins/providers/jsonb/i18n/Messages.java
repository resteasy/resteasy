package org.jboss.resteasy.plugins.providers.jsonb.i18n;

import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.Message.Format;
import org.jboss.logging.annotations.MessageBundle;

/**
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 8200;

   @Message(id = BASE + 00, value = "JSON Binding deserialization error", format=Format.MESSAGE_FORMAT)
   String jsonBDeserializationError(@Cause Throwable element);

   @Message(id = BASE + 05, value = "JSON Binding serialization error {0}", format=Format.MESSAGE_FORMAT)
   String jsonBSerializationError(String element);
}