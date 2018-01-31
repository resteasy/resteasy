package org.jboss.resteasy.test.providers.injection.resource;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("app1")
public class ApplicationInjectionApplication1 extends ApplicationInjectionApplicationParent {
   
   public String getName() {
      return "ApplicationInjectionApplication1";
   }
}
