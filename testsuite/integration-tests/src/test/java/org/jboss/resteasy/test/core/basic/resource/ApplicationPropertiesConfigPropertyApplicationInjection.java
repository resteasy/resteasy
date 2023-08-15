package org.jboss.resteasy.test.core.basic.resource;

import java.util.Collections;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import org.jboss.resteasy.test.annotations.FollowUpRequired;

@ApplicationPath("/")
@ApplicationScoped
@FollowUpRequired("The @ApplicationScope annotation can be removed once @ApplicationPath is a bean defining annotation.")
public class ApplicationPropertiesConfigPropertyApplicationInjection extends Application {

    @Override
    public Map<String, Object> getProperties() {
        return Collections.<String, Object> singletonMap("Prop1", "Value1");
    }
}
