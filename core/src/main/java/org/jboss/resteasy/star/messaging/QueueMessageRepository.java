package org.jboss.resteasy.star.messaging;

import javax.ws.rs.core.MultivaluedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueueMessageRepository
{
   private ConcurrentHashMap<Long, QueuedMessage> repository = new ConcurrentHashMap<Long, QueuedMessage>();
   private AtomicLong counter = new AtomicLong(0);

   public QueuedMessage getMessage(long id)
   {
      return repository.get(id);
   }

   public Message addMessage(MultivaluedMap<String, String> headers, byte[] body)
   {
      QueuedMessage msg = new QueuedMessage();
      msg.setId(counter.getAndIncrement());
      msg.setBody(body);
      msg.setHeaders(headers);
      repository.put(msg.getId(), msg);
      return msg;
   }
}