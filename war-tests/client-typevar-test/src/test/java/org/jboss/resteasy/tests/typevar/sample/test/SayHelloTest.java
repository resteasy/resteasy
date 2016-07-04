package org.jboss.resteasy.tests.typevar.sample.test;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.tests.typevar.sample.HelloString;
import org.junit.Assert;
import org.junit.Test;

public class SayHelloTest {

	   @Test
	   public void testEcho()
	   {
		   ResteasyClient client = new ResteasyClientBuilder().build();
		   ResteasyWebTarget target = client.target("http://localhost:9095");
		   HelloString proxy = target.proxy(HelloString.class);
		   String hello = proxy.sayHi("hello");
		   Assert.assertEquals("hello", hello);
	   }

	   
}
