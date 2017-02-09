package org.jboss.resteasy.plugins.providers.sse.i18n;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * 
 * 
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 19500;
   @Message(id = BASE + 0, value = "WebTarget is not set for creating SseEventSource")
   String webTargetIsNotSetForEventSource();
   @Message(id = BASE + 1, value = "EventSource is not ready to open")
   String eventSourceIsNotReadyForOpen();
   @Message(id = BASE + 2, value = "No suitable message body writer for class : %s")
   String notFoundMBW(String className);
   @Message(id = BASE + 3, value = "Sever sent event feature requries HttpServlet30Dispatcher")
   String asyncServletIsRequired();
   @Message(id = BASE + 4, value = "Failed to read SseEvent")
   String readEventException();
   @Message(id = BASE + 5, value = "%s is not set for OutboundSseEvent builder")
   String nullValueSetToCreateOutboundSseEvent(String field);
   @Message(id = BASE + 6, value = "Failed to write data to InBoundSseEvent")
   String failedToWriteDataToInboudEvent();
   @Message(id = BASE + 7, value = "No suitable message body reader for class : %s")
   String notFoundMBR(String className);
   @Message(id = BASE + 8, value = "Failed to read data from InboundSseEvent")
   String failedToReadData();
   @Message(id = BASE + 9, value = "Failed to create SseEventOutput")
   String failedToCreateSseEventOutput();

}
