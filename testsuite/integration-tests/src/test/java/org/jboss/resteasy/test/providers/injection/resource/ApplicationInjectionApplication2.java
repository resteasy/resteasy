package org.jboss.resteasy.test.providers.injection.resource;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("app2")
public class ApplicationInjectionApplication2 extends ApplicationInjectionApplicationParent {
   
   public String getName() {
      return "ApplicationInjectionApplication2";
   }
}
