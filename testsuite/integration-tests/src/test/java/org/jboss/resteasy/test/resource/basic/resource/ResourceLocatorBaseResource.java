package org.jboss.resteasy.test.resource.basic.resource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;
import org.junit.Assert;

@Path("/")
public class ResourceLocatorBaseResource {

    private static final Logger LOG = Logger.getLogger(ResourceLocatorBaseResource.class);

    @Path("base/{param}/resources")
    public Object getSubresource(@PathParam("param") String param, @Context UriInfo uri) {
        LOG.info("Here in BaseResource");
        Assert.assertEquals("1", param);
        List<String> matchedURIs = uri.getMatchedURIs();
        Assert.assertEquals(2, matchedURIs.size());
        Assert.assertEquals("base/1/resources", matchedURIs.get(0));
        Assert.assertEquals("", matchedURIs.get(1));
        for (String ancestor : matchedURIs)
            LOG.info("   " + ancestor);

        LOG.info("Uri Ancesstors Object for Subresource.doGet():");
        Assert.assertEquals(1, uri.getMatchedResources().size());
        Assert.assertEquals(ResourceLocatorBaseResource.class, uri.getMatchedResources().get(0).getClass());
        return new ResourceLocatorSubresource();
    }

    @Path("proxy")
    public ResourceLocatorSubresource3Interface sub3() {
        return (ResourceLocatorSubresource3Interface) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class<?>[] { ResourceLocatorSubresource3Interface.class }, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return method.invoke(new ResourceLocatorSubresource3(), args);
                    }
                });
    }

}
