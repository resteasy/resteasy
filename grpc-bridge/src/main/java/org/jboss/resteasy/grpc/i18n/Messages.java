package org.jboss.resteasy.grpc.i18n;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * 
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 21000;

   @Message(id = 10028, value = "Async processing already started")
   IllegalStateException asyncAlreadyStarted();
   
   @Message(id = BASE + 05, value = "Async not started")
   IllegalStateException asyncNotStarted();
   
   @Message(id = BASE + 10, value = "Async processing already started")
   IllegalStateException asyncProcessingAlreadyStarted();
   
   @Message(id = BASE + 15, value = "Expected Message, got %s")
   String expectedMessage(Class<?> clazz);
   
   @Message(id = BASE + 20, value = "No suitable message body writer for class : %s")
   String notFoundMBW(String className);
   
   @Message(id = BASE + 25, value = "Request %s was not original or a wrapper")
   IllegalArgumentException requestWasNotOriginalOrWrapper(ServletRequest request);

   @Message(id = BASE + 30, value = "Response %s was not original or a wrapper")
   IllegalArgumentException responseWasNotOriginalOrWrapper(ServletResponse response);
   
}
