package org.jboss.resteasy.resteasy801;

import java.lang.reflect.Proxy;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class TestResource
{  
   @Produces("text/plain")
   @Path("test")
   public TestSubResourceSubIntf resourceLocator()
   {
      Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                                            new Class[]{TestSubResourceSubIntf.class}, 
                                            new TestInvocationHandler());
      
      return TestSubResourceSubIntf.class.cast(proxy);
   }
}
