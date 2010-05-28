package org.jboss.resteasy.star.messaging.topic;

import org.jboss.resteasy.star.messaging.queue.push.PushStore;

import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface TopicPushStore extends PushStore
{
   List<PushTopicRegistration> getByTopic(String topic);
}
