package org.jboss.resteasy.test.core.basic.resource;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.Collections;
import java.util.Map;

@ApplicationPath("/")
public class ApplicationPropertiesConfig extends Application {

   @Override
   public Map<String, Object> getProperties() {
      return Collections.<String, Object>singletonMap("Prop1", "Value1");
   }
}
