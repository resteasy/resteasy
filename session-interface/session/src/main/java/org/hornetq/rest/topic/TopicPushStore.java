package org.hornetq.rest.topic;

import org.hornetq.rest.queue.push.PushStore;

import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface TopicPushStore extends PushStore
{
   List<PushTopicRegistration> getByTopic(String topic);
}
