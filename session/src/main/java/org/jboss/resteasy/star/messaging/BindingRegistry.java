package org.jboss.resteasy.star.messaging;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface BindingRegistry
{
   Object lookup(String name);

   boolean bind(String name, Object obj);

   void unbind(String name);

   void close();
}
