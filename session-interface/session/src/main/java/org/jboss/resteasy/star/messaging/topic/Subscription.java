package org.jboss.resteasy.star.messaging.topic;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface Subscription
{
   boolean isDurable();

   void setDurable(boolean isDurable);
}
