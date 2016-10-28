package org.jboss.resteasy.test.resource.path.resource;

import org.junit.Assert;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

@Path("/a1")
public class ResourceLocatorWithBaseNoExpressionResource {
    public static final String ERROR_MSG = "Wrong URI";
    @Path("base/{param}/resources")
    public Object getSubresource(@PathParam("param") String param, @Context UriInfo uri) {
        Assert.assertEquals("1", param);
        List<String> matchedURIs = uri.getMatchedURIs();
        Assert.assertEquals(ERROR_MSG, 2, matchedURIs.size());
        Assert.assertEquals(ERROR_MSG, "a1/base/1/resources", matchedURIs.get(0));
        Assert.assertEquals(ERROR_MSG, "a1", matchedURIs.get(1));
        Assert.assertEquals(ERROR_MSG, 1, uri.getMatchedResources().size());
        Assert.assertEquals(ERROR_MSG, ResourceLocatorWithBaseNoExpressionResource.class, uri.getMatchedResources().get(0).getClass());
        return new ResourceLocatorWithBaseNoExpressionSubresource();

    }

    @Path("proxy")
    public ResourceLocatorWithBaseNoExpressionSubresource3Interface sub3() {

        return (ResourceLocatorWithBaseNoExpressionSubresource3Interface) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[]{ResourceLocatorWithBaseNoExpressionSubresource3Interface.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return method.invoke(new ResourceLocatorWithBaseNoExpressionSubresource3(), args);
            }
        });
    }
}
