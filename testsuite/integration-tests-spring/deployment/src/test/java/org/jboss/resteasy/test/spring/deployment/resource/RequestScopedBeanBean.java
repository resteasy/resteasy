package org.jboss.resteasy.test.spring.deployment.resource;

public class RequestScopedBeanBean {

   public RequestScopedBeanInnerBean getMyInnerBean() {
      return new RequestScopedBeanInnerBeanImpl();
   }
}
