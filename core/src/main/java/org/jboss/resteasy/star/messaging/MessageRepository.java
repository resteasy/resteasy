package org.jboss.resteasy.star.messaging;

import javax.ws.rs.core.MultivaluedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MessageRepository
{
   private ConcurrentHashMap<Long, MessageIndex> messageIndex = new ConcurrentHashMap<Long, MessageIndex>();
   private ConcurrentHashMap<Long, Message> repository = new ConcurrentHashMap<Long, Message>();
   private AtomicLong counter = new AtomicLong(0);

   public Message getMessage(long id)
   {
      return repository.get(id);
   }

   public MessageIndex getMessageIndex(long id)
   {
      return messageIndex.get(id);
   }

   public Message addMessage(MultivaluedMap<String, String> headers, byte[] body)
   {
      Message msg = new Message();
      msg.setId(counter.getAndIncrement());
      msg.setBody(body);
      msg.setHeaders(headers);
      repository.put(msg.getId(), msg);
      return msg;
   }

   public MessageIndex addIndex(long id)
   {
      MessageIndex index = new MessageIndex();
      index.setId(id);
      messageIndex.put(id, index);
      return index;
   }

}
