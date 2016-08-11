package org.jboss.resteasy.test.resource.basic.resource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.junit.Assert;

@Path("/")
public class ResourceLocatorBaseResource {
   
   @Path("base/{param}/resources")
   public Object getSubresource(@PathParam("param") String param, @Context UriInfo uri) {
      System.out.println("Here in BaseResource");
      Assert.assertEquals("1", param);
      List<String> matchedURIs = uri.getMatchedURIs();
      Assert.assertEquals(2, matchedURIs.size());
      Assert.assertEquals("base/1/resources", matchedURIs.get(0));
      Assert.assertEquals("", matchedURIs.get(1));
      for (String ancestor : matchedURIs) System.out.println("   " + ancestor);

      System.out.println("Uri Ancesstors Object for Subresource.doGet():");
      Assert.assertEquals(1, uri.getMatchedResources().size());
      Assert.assertEquals(ResourceLocatorBaseResource.class, uri.getMatchedResources().get(0).getClass());
      return new ResourceLocatorSubresource();
   }
   
   @Path("proxy")
   public ResourceLocatorSubresource3Interface sub3() {
      return (ResourceLocatorSubresource3Interface) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[]{ResourceLocatorSubresource3Interface.class}, new InvocationHandler()
      {
         @Override
         public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
         {
            return method.invoke(new ResourceLocatorSubresource3(), args);
         }
      });
   }

}
