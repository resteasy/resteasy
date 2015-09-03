package org.jboss.resteasy.plugins.providers.multipart.i18n;

import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;

import org.apache.james.mime4j.parser.Field;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.Message.Format;
import org.jboss.logging.annotations.MessageBundle;
import org.jboss.resteasy.plugins.providers.multipart.AbstractMultipartWriter;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 25, 2015
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 7500;

   @Message(id = BASE + 0, value = "Could find no Content-Disposition header within part")
   String couldFindNoContentDispositionHeader();

   @Message(id = BASE + 5, value = "Could not parse Content-Disposition for MultipartFormData: {0}", format=Format.MESSAGE_FORMAT)
   String couldNotParseContentDisposition(Field field);

   @Message(id = BASE + 10, value = "This DataSource represents an incoming xop message part. Getting an OutputStream on it is not allowed.")
   String dataSourceRepresentsXopMessagePart();

   @Message(id = BASE + 15, value = "Exception while extracting attachment with cid = %s from xop message to a byte[].")
   String exceptionWhileExtractionAttachment(String cid);
   
   @Message(id = BASE + 20, value = "Had to write out multipartoutput = {0} with writer = {1} but this writer can only handle {2}", format=Format.MESSAGE_FORMAT)
   String hadToWriteMultipartOutput(MultipartOutput multipartOutput, AbstractMultipartWriter writer, Class<?> clazz);

   @Message(id = BASE + 25, value = "No attachment with cid = {0} (Content-ID = {1}) found in xop message.", format=Format.MESSAGE_FORMAT)
   String noAttachmentFound(String cid, String contentId);

   @Message(id = BASE + 30, value = "This provider and this method are not meant for stand alone usage.")
   String notMeantForStandaloneUsage();
   
   @Message(id = BASE + 35, value = "Reader = {0} received genericType = {1}, but it is not instance of {2}", format=Format.MESSAGE_FORMAT)
   String receivedGenericType(MessageBodyReader<?> reader, Type genericType, Class<?> clazz);

   @Message(id = BASE + 40, value = "SwaRefs are not supported in xop creation.")
   String swaRefsNotSupported();
   
   @Message(id = BASE + 45, value = "Unable to find a MessageBodyReader for media type: {0} and class type {1}", format=Format.MESSAGE_FORMAT)
   String unableToFindMessageBodyReader(MediaType mediaType, String type);
   
   @Message(id = BASE + 50, value = "Unable to get boundary for multipart")
   String unableToGetBoundary();

   @Message(id = BASE + 55, value = "java.net.URLDecoder does not support UTF-8 encoding")
   String urlDecoderDoesNotSupportUtf8();
   
   @Message(id = BASE + 60, value = "java.net.URLEncoder does not support UTF-8 encoding")
   String urlEncoderDoesNotSupportUtf8();
}
