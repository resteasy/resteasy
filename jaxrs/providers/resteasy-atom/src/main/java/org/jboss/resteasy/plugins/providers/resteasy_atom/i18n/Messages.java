package org.jboss.resteasy.plugins.providers.resteasy_atom.i18n;

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
   int BASE = 5000;
   
   @Message(id = BASE + 0, value = "This constructor must be called in the context of a JAX-RS request")
   String consructorMustBeCalled();
   
   @Message(id = BASE + 5, value = "Unable to find JAXBContext for media type: %s")
   String unableToFindJAXBContext(MediaType mediaType);

   @Message(id = BASE + 10, value = "Unable to marshal: %s")
   String unableToMarshal(MediaType mediaType);
   
   @Message(id = BASE + 15, value = "Unable to unmarshal: %s")
   String unableToUnmarshal(MediaType mediaType);
}
