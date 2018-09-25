package org.jboss.resteasy.core;

public interface ResourceMethodInvokerAwareResponse {

   ResourceMethodInvoker getMethod();

   void setMethod(ResourceMethodInvoker method);


}
