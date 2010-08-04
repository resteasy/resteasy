package org.hornetq.rest.queue.push;

import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.rest.queue.push.xml.PushRegistration;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface PushStrategy
{
   public boolean push(ClientMessage message);

   public void setRegistration(PushRegistration reg);

   public void start() throws Exception;

   public void stop() throws Exception;
}
