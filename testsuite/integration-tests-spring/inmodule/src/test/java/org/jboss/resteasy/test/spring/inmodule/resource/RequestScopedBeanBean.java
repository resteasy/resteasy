package org.jboss.resteasy.test.spring.inmodule.resource;

public class RequestScopedBeanBean {

   public RequestScopedBeanInnerBean getMyInnerBean() {
      return new RequestScopedBeanInnerBeanImpl();
   }
}
