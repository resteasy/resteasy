package org.jboss.resteasy.links.test.el;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.links.RESTServiceDiscovery.AtomLink;
import org.jboss.resteasy.links.test.Book;
import org.jboss.resteasy.links.test.BookStoreService;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestLinksNoPackage
{

   private static NettyJaxrsServer server;
   private static Dispatcher dispatcher;

   @BeforeClass
   public static void beforeClass() throws Exception
   {
      server = new NettyJaxrsServer();
      server.setPort(TestPortProvider.getPort());
      server.setRootResourcePath("/");
      server.start();
      dispatcher = server.getDeployment().getDispatcher();
   }

   @AfterClass
   public static void afterClass() throws Exception
   {
      server.stop();
      server = null;
      dispatcher = null;
   }

	@Parameters
	public static List<Class<?>[]> getParameters(){
		List<Class<?>[]> classes = new ArrayList<Class<?>[]>();
		classes.add(new Class<?>[]{BookStoreNoPackage.class});
		return classes;
	}

	private Class<?> resourceType;
	private String url;
	private BookStoreService client;
	private HttpClient httpClient;
	
	public TestLinksNoPackage(Class<?> resourceType){
		this.resourceType = resourceType;
	}
	
	@Before
	public void before(){
		POJOResourceFactory noDefaults = new POJOResourceFactory(resourceType);
		dispatcher.getRegistry().addResourceFactory(noDefaults);
		httpClient = HttpClientBuilder.create().build();
		ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(httpClient);
		url = generateBaseUrl();
		ResteasyWebTarget target = new ResteasyClientBuilder().httpEngine(engine).build().target(url);
		client = target.proxy(BookStoreService.class);
	}

	@SuppressWarnings("deprecation")
    @After
	public void after(){
		// TJWS does not support chunk encodings well so I need to kill kept
		// alive connections
		httpClient.getConnectionManager().closeIdleConnections(0, TimeUnit.MILLISECONDS);
		dispatcher.getRegistry().removeRegistrations(resourceType);
	}
	
	@Test
	public void testELWorksWithoutPackage() throws Exception
	{
		Book book = client.getBookXML("foo");
		checkBookLinks1(url, book);
		book = client.getBookJSON("foo");
		checkBookLinks1(url, book);
	}

	private void checkBookLinks1(String url, Book book) {
		Assert.assertNotNull(book);
		Assert.assertEquals("foo", book.getTitle());
		Assert.assertEquals("bar", book.getAuthor());
		RESTServiceDiscovery links = book.getRest();
		Assert.assertNotNull(links);
		Assert.assertEquals(1, links.size());
		// self
		AtomLink atomLink = links.getLinkForRel("self");
		Assert.assertNotNull(atomLink);
		Assert.assertEquals(url+"/book/foo", atomLink.getHref());
	}
}