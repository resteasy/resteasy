package org.jboss.resteasy.plugins.providers.jaxb.json.i18n;

import javax.ws.rs.core.MediaType;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 24, 2015
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 7000;
   
   @Message(id = BASE + 00, value = "Was expecting a ':' in json map")
   String expectingColonMap();
   
   @Message(id = BASE + 05, value = "Was expecting a ',' in json array")
   String expectingCommaJsonArray();
   
   @Message(id = BASE + 10, value = "Expecting a json array as input")
   String expectingJsonArray();
   
   @Message(id = BASE + 15, value = "Expecting '{' in json map")
   String expectingLeftBraceJsonMap();
   
   @Message(id = BASE + 20, value = "Expecting '\"' in json map key")
   String expectingQuote();
   
   @Message(id = BASE + 25, value = "Expecting a StreamSource")
   String expectingStreamSource();

   @Message(id = BASE + 30, value = "Unable to find JAXBContext for media type: %s")
   String unableToFindJAXBContext(MediaType mediaType);
   
   @Message(id = BASE + 35, value = "Unexpected end of json input")
   String unexpectedEndOfJsonInput();
   
   @Message(id = BASE + 40, value = "Unexpected end of stream")
   String unexpectedEndOfStream();
}
