package org.jboss.resteasy.core;

/**
 *
 * @author Nicolas NESMON
 *
 */
public interface PostResourceMethodInvoker extends AutoCloseable {
   void invoke();
}
