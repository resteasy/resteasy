package org.jboss.resteasy.test.core.basic.resource;


import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.Map;

@ApplicationPath("/")
public class ApplicationPropertiesConfigPropertyApplicationInjection extends Application {

   @Override
   public Map<String, Object> getProperties() {
      return Collections.<String, Object>singletonMap("Prop1", "Value1");
   }
}
