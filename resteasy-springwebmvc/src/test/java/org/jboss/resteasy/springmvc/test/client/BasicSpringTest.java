package org.jboss.resteasy.springmvc.test.client;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.springmvc.tjws.TJWSEmbeddedSpringMVCServer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class BasicSpringTest {

	static TJWSEmbeddedSpringMVCServer server = null;
	static long start;

	@BeforeClass
	public static void before() {
		start = System.currentTimeMillis();
		server = new TJWSEmbeddedSpringMVCServer("classpath:spring-test1.xml", 8081);
		server.start();
	}

	@AfterClass
	public static void after() {
		if (server != null) {
			try{
				server.stop();
			}catch(RuntimeException e){
				e.printStackTrace();
			}
			server = null;
			System.out.println("Total time in ms: " + (System.currentTimeMillis() - start));
		}
	}

	@Test
	public void testBasic() throws HttpException, IOException {
		// ResteasyProviderFactory has already been updated as part of Spring
		BasicResource br = ProxyFactory.create(BasicResource.class,
				"http://localhost:8081");
		Assert.assertEquals("/basic/url", br.getURL());
		Assert.assertEquals("test", br.getBasicString());
		Assert.assertEquals("test", br.getBasicObject().getSomething());
		Assert.assertEquals("Hi, I'm custom!", br.getCustomRepresentation());
		Assert.assertEquals(1, br.getSingletonCount().intValue());
		Assert.assertEquals(2, br.getSingletonCount().intValue());
		Assert.assertEquals(1, br.getPrototypeCount().intValue());
		Assert.assertEquals(1, br.getPrototypeCount().intValue());
		Assert.assertEquals("text/plain", br.getContentTypeHeader());
	}
}
