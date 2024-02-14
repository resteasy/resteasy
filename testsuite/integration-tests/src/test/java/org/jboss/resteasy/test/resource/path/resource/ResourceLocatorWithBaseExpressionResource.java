package org.jboss.resteasy.test.resource.path.resource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import org.junit.jupiter.api.Assertions;

@Path("/a{x:\\d}")
public class ResourceLocatorWithBaseExpressionResource {
    public static final String ERROR_MSG = "Wrong URI";

    @Path("base/{param}/resources")
    public Object getSubresource(@PathParam("param") String param, @Context UriInfo uri) {
        Assertions.assertEquals("1", param);
        List<String> matchedURIs = uri.getMatchedURIs();
        Assertions.assertEquals(2, matchedURIs.size(), ERROR_MSG);
        Assertions.assertEquals("a1/base/1/resources", matchedURIs.get(0), ERROR_MSG);
        Assertions.assertEquals("a1", matchedURIs.get(1), ERROR_MSG);
        Assertions.assertEquals(1, uri.getMatchedResources().size(), ERROR_MSG);
        Assertions.assertEquals(ResourceLocatorWithBaseExpressionResource.class,
                uri.getMatchedResources().get(0).getClass(), ERROR_MSG);
        return new ResourceLocatorWithBaseExpressionSubresource();

    }

    @Path("proxy")
    public ResourceLocatorWithBaseExpressionSubresource3Interface sub3() {

        return (ResourceLocatorWithBaseExpressionSubresource3Interface) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class<?>[] { ResourceLocatorWithBaseExpressionSubresource3Interface.class }, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return method.invoke(new ResourceLocatorWithBaseExpressionSubresource3(), args);
                    }
                });
    }
}
