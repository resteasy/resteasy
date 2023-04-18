package org.jboss.resteasy.test.core.basic.resource;

import java.util.Collections;
import java.util.Map;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/")
public class ApplicationPropertiesConfigPropertyApplicationInjection extends Application {

    @Override
    public Map<String, Object> getProperties() {
        return Collections.<String, Object> singletonMap("Prop1", "Value1");
    }
}
