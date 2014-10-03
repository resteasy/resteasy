package org.jboss.resteasy.i18n;

import javax.ws.rs.core.MediaType;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * 
 * Copyright October 2, 2014
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);

   @Message(id = 2000, value = "This constructor must be called in the context of a JAX-RS request")
   String consructorMustBeCalled();
   
   @Message(id = 2005, value = "Unable to find JAXBContext for media type: %s")
   String unableToFindJAXBContext(MediaType mediaType);

   @Message(id = 2010, value = "Unable to marshal: %s")
   String unableToMarshal(MediaType mediaType);
   
   @Message(id = 2015, value = "Unable to unmarshal: %s")
   String unableToUnmarshal(MediaType mediaType);
}
