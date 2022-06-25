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

   @Message(id = BASE + 12, value = "Default Application class not implemented yet")
   String defaultApplicationNotImplemented();
   
   @Message(id = BASE + 15, value = "Expected Message, got %s")
   String expectedMessage(Class<?> clazz);

   @Message(id = BASE + 17, value = "Cannot call getWriter(), getOutputStream() already called")
   IllegalStateException getOutputStreamAlreadyCalled();

   @Message(id = BASE + 18, value = "Cannot call getOutputStream(), getWriter() already called")
   IllegalStateException getWriterAlreadyCalled();

   @Message(id = BASE + 20, value = "Header name was null")
   NullPointerException headerNameWasNull();

   @Message(id = BASE + 25, value = "Header %s cannot be converted to a date")
   IllegalArgumentException headerCannotBeConvertedToDate(String header);

   @Message(id = BASE + 30, value = "InputStream already returned")
   String inputStreamAlreadyReturned();

   @Message(id = BASE + 35, value = "Method %s is not implemented")
   String isNotImplemented(String method);

   @Message(id = BASE + 40, value = "No suitable message body writer for class : %s")
   String notFoundMBW(String className);

   @Message(id = BASE + 45, value = "Reader already returned")
   IllegalArgumentException readerAlreadyReturned();

   @Message(id = BASE + 50, value = "Request %s was not original or a wrapper")
   IllegalArgumentException requestWasNotOriginalOrWrapper(ServletRequest request);

   @Message(id = BASE + 55, value = "Response %s was not original or a wrapper")
   IllegalArgumentException responseWasNotOriginalOrWrapper(ServletResponse response);

}
