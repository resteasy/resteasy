package org.hornetq.rest.queue.push;

import org.hornetq.api.core.client.ClientMessage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UriTemplateStrategy extends UriStrategy
{
   protected String createUri(ClientMessage message)
   {
      String dupId = registration.getId() + "-" + message.getMessageID() + "-" + message.getTimestamp();
      String uri = targetUri.build(dupId).toString();
      return uri;
   }
}
