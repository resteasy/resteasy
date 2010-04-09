package org.jboss.resteasy.star.messaging;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface MessageRepository<M extends Message>
{
   String generateId();

   M createMessage(MultivaluedMap<String, String> headers, byte[] body);

   M createMessage(String id, MultivaluedMap<String, String> headers, byte[] body);

   M getMessage(String id);

   URI getMessageUri(String id, UriInfo uriInfo);
}
