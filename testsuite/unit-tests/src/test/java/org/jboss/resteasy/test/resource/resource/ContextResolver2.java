package org.jboss.resteasy.test.resource.resource;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
@Produces("text/plain")
public class ContextResolver2 implements ContextResolver<String> {
    public String getContext(Class<?> type) {
        if (type.equals(int.class)) {
            return "2";
        }
        return null;
    }
}
