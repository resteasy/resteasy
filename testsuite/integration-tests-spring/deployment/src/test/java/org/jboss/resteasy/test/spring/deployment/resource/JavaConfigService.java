package org.jboss.resteasy.test.spring.deployment.resource;

/**
 * This POJO service bean will get injected into the resource.
 */
public class JavaConfigService {
   public String invoke() {
      return "hello";
   }
}
