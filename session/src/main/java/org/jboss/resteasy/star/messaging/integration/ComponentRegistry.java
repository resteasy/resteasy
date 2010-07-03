package org.jboss.resteasy.star.messaging.integration;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ComponentRegistry
{
   Object lookup(String name);

   boolean bind(String name, Object obj);

   void unbind(String name);

   void close();
}
