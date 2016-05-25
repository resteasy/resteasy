package org.jboss.resteasy.links.test;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.links.RESTServiceDiscovery.AtomLink;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

public class TestLinkIds
{

	private static Dispatcher dispatcher;

	@BeforeClass
	public static void beforeClass() throws Exception
	{
		dispatcher = EmbeddedContainer.start().getDispatcher();
		POJOResourceFactory noDefaults = new POJOResourceFactory(IDServiceTestBean.class);
		dispatcher.getRegistry().addResourceFactory(noDefaults);
		httpClient = new DefaultHttpClient();
		ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(httpClient);
		url = generateBaseUrl();
		client = ProxyFactory.create(IDServiceTest.class, url,
					executor);
	}

	@AfterClass
	public static void afterClass() throws Exception
	{
		EmbeddedContainer.stop();
	}

	private static Class<?> resourceType;
	private static String url;
	private static IDServiceTest client;
	private static HttpClient httpClient;
	
	@After
	public void after(){
		// TJWS does not support chunk encodings well so I need to kill kept
		// alive connections
		httpClient.getConnectionManager().closeIdleConnections(0, TimeUnit.MILLISECONDS);
	}
	
	@Test
	public void testResourceId() throws Exception
	{
		IdBook book = client.getResourceIdBook("foo");
		checkBook(book, "/resource-id/book/foo");
	}

	@Test
	public void testResourceIds() throws Exception
	{
		IdBook book = client.getResourceIdsBook("foo", "bar");
		checkBook(book, "/resource-ids/book/foo/bar");
	}

	@Test
	public void testResourceIdMethod() throws Exception
	{
		IdBook book = client.getResourceIdMethodBook("foo");
		checkBook(book, "/resource-id-method/book/foo");
	}

	@Test
	public void testResourceIdsMethod() throws Exception
	{
		IdBook book = client.getResourceIdsMethodBook("foo", "bar");
		checkBook(book, "/resource-ids-method/book/foo/bar");
	}

	@Test
	public void testXmlId() throws Exception
	{
		IdBook book = client.getXmlIdBook("foo");
		checkBook(book, "/xml-id/book/foo");
	}

	@Test
	public void testJpaId() throws Exception
	{
		IdBook book = client.getJpaIdBook("foo");
		checkBook(book, "/jpa-id/book/foo");
	}

	private void checkBook(IdBook book, String relativeUrl) {
		Assert.assertNotNull(book);
		RESTServiceDiscovery links = book.getRest();
		Assert.assertNotNull(links);
		Assert.assertEquals(1, links.size());
		AtomLink link = links.get(0);
		Assert.assertEquals("self", link.getRel());
		Assert.assertEquals(url + relativeUrl, link.getHref());
	}
}