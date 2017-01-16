package org.jboss.resteasy.links.test;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.links.RESTServiceDiscovery.AtomLink;
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
public class TestLinks
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
		return Arrays.asList(new Class<?>[]{BookStore.class}, new Class<?>[]{BookStoreMinimal.class});
	}

	private Class<?> resourceType;
	private String url;
	private BookStoreService client;
	private HttpClient httpClient;
	
	public TestLinks(Class<?> resourceType){
		this.resourceType = resourceType;
	}
	
	@Before
	public void before(){
		POJOResourceFactory noDefaults = new POJOResourceFactory(resourceType);
		dispatcher.getRegistry().addResourceFactory(noDefaults);
		httpClient = new DefaultHttpClient();
		ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(httpClient);
		url = generateBaseUrl();
		client = ProxyFactory.create(BookStoreService.class, url,
					executor);
	}

	@After
	public void after(){
		// TJWS does not support chunk encodings well so I need to kill kept
		// alive connections
		httpClient.getConnectionManager().closeIdleConnections(0, TimeUnit.MILLISECONDS);
		dispatcher.getRegistry().removeRegistrations(resourceType);
	}
	
	@Test
	public void testLinks() throws Exception
	{
		Book book = client.getBookXML("foo");
		checkBookLinks1(url, book);
		book = client.getBookJSON("foo");
		checkBookLinks1(url, book);
	}

	@Test
	public void testComments() throws Exception
	{
		List<Comment> comments = client.getBookCommentsXML("foo");
		Assert.assertNotNull(comments);
		Assert.assertFalse(comments.isEmpty());
		checkCommentLinks(url, comments.get(0));
		comments = client.getBookCommentsJSON("foo");
		Assert.assertNotNull(comments);
		Assert.assertFalse(comments.isEmpty());
		checkCommentLinks(url, comments.get(0));
	}

	private void checkBookLinks1(String url, Book book) {
		Assert.assertNotNull(book);
		Assert.assertEquals("foo", book.getTitle());
		Assert.assertEquals("bar", book.getAuthor());
		RESTServiceDiscovery links = book.getRest();
		Assert.assertNotNull(links);
		Assert.assertEquals(7, links.size());
		// self
		AtomLink atomLink = links.getLinkForRel("self");
		Assert.assertNotNull(atomLink);
		Assert.assertEquals(url+"/book/foo", atomLink.getHref());
		// update
		atomLink = links.getLinkForRel("update");
		Assert.assertNotNull(atomLink);
		Assert.assertEquals(url+"/book/foo", atomLink.getHref());
		// remove
		atomLink = links.getLinkForRel("remove");
		Assert.assertNotNull(atomLink);
		Assert.assertEquals(url+"/book/foo", atomLink.getHref());
		// list
		atomLink = links.getLinkForRel("list");
		Assert.assertNotNull(atomLink);
		Assert.assertEquals(url+"/books", atomLink.getHref());
		// add
		atomLink = links.getLinkForRel("add");
		Assert.assertNotNull(atomLink);
		Assert.assertEquals(url+"/books", atomLink.getHref());
		// comments
		atomLink = links.getLinkForRel("comments");
		Assert.assertNotNull(atomLink);
		Assert.assertEquals(url+"/book/foo/comments", atomLink.getHref());
		// comment collection
		atomLink = links.getLinkForRel("comment-collection");
		Assert.assertNotNull(atomLink);
		Assert.assertEquals(url+"/book/foo/comment-collection", atomLink.getHref());
	}

	private void checkCommentLinks(String url, Comment comment) {
		Assert.assertNotNull(comment);
		Assert.assertEquals(0, comment.getId());
		RESTServiceDiscovery links = comment.getRest();
		Assert.assertNotNull(links);
		Assert.assertEquals(6, links.size());
		// self
		AtomLink atomLink = links.getLinkForRel("self");
		Assert.assertNotNull(atomLink);
		Assert.assertEquals(url+"/book/foo/comment/0", atomLink.getHref());
		// update
		atomLink = links.getLinkForRel("update");
		Assert.assertNotNull(atomLink);
		Assert.assertEquals(url+"/book/foo/comment/0", atomLink.getHref());
		// remove
		atomLink = links.getLinkForRel("remove");
		Assert.assertNotNull(atomLink);
		Assert.assertEquals(url+"/book/foo/comment/0", atomLink.getHref());
		// list
		atomLink = links.getLinkForRel("list");
		Assert.assertNotNull(atomLink);
		Assert.assertEquals(url+"/book/foo/comments", atomLink.getHref());
		// add
		atomLink = links.getLinkForRel("add");
		Assert.assertNotNull(atomLink);
		Assert.assertEquals(url+"/book/foo/comments", atomLink.getHref());
		// collection
		atomLink = links.getLinkForRel("collection");
		Assert.assertNotNull(atomLink);
		Assert.assertEquals(url+"/book/foo/comment-collection", atomLink.getHref());
	}
}