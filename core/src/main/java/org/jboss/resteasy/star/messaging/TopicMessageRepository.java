package org.jboss.resteasy.star.messaging;

import javax.ws.rs.core.MultivaluedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TopicMessageRepository
{
   private ConcurrentHashMap<Long, TopicMessageIndex> messageIndex = new ConcurrentHashMap<Long, TopicMessageIndex>();
   private ConcurrentHashMap<Long, Message> repository = new ConcurrentHashMap<Long, Message>();
   private AtomicLong counter = new AtomicLong(0);

   public Message getMessage(long id)
   {
      return repository.get(id);
   }

   public TopicMessageIndex getMessageIndex(long id)
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

   public TopicMessageIndex addIndex(long id)
   {
      TopicMessageIndex index = new TopicMessageIndex();
      index.setId(id);
      messageIndex.put(id, index);
      return index;
   }

}
