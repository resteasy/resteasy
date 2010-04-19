package org.jboss.resteasy.links.test;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.embedded.SimplePrincipal;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.test.EmbeddedContainer;
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
public class TestSecureLinks
{

	private static Dispatcher dispatcher;

	@BeforeClass
	public static void beforeClass() throws Exception
	{
		dispatcher = EmbeddedContainer.start("/", new SecurityDomain(){

			public Principal authenticate(String username, String password)
					throws SecurityException {
				return new SimplePrincipal(username);
			}

			public boolean isUserInRoll(Principal username, String role) {
				return username.getName().equals(role);
			}
			
		}).getDispatcher();
	}

	@AfterClass
	public static void afterClass() throws Exception
	{
		EmbeddedContainer.stop();
	}

	@Parameters
	public static List<Class<?>[]> getParameters(){
		return Arrays.asList(new Class<?>[]{SecureBookStore.class}, new Class<?>[]{SecureBookStoreMinimal.class});
	}

	private Class<?> resourceType;
	private String url;
	private BookStoreService client;
	private HttpClient httpClient;
	
	public TestSecureLinks(Class<?> resourceType){
		this.resourceType = resourceType;
	}

	@Before
	public void before(){
		POJOResourceFactory noDefaults = new POJOResourceFactory(resourceType);
		dispatcher.getRegistry().addResourceFactory(noDefaults);
		httpClient = new HttpClient();
		httpClient.getState().setAuthenticationPreemptive(true);
		ApacheHttpClientExecutor executor = new ApacheHttpClientExecutor(httpClient);
		url = generateBaseUrl();
		client = ProxyFactory.create(BookStoreService.class, url,
					executor);
	}

	@After
	public void after(){
		// TJWS does not support chunk encodings well so I need to kill kept
		// alive connections
		httpClient.getHttpConnectionManager().closeIdleConnections(0);
		dispatcher.getRegistry().removeRegistrations(resourceType);
	}
	
	@Test
	public void testSecureLinksAdmin() throws Exception
	{
		httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("admin", "asd"));
		Book book = client.getBookXML("foo");
		checkBookLinks1(url, book, "add", "update", "list", "self", "remove");
	}

	@Test
	public void testSecureLinksPowerUser() throws Exception
	{
		httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("power-user", "asd"));
		Book book = client.getBookXML("foo");
		checkBookLinks1(url, book, "add", "update", "list", "self");
	}

	@Test
	public void testSecureLinksUser() throws Exception
	{
		httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("user", "asd"));
		Book book = client.getBookXML("foo");
		checkBookLinks1(url, book, "list", "self");
	}

	private void checkBookLinks1(String url, Book book, String... expectedLinks) {
		Assert.assertNotNull(book);
		Assert.assertEquals("foo", book.getTitle());
		Assert.assertEquals("bar", book.getAuthor());
		RESTServiceDiscovery links = book.getRest();
		Assert.assertNotNull(links);
		Assert.assertEquals(expectedLinks.length, links.size());
		for (String expectedLink : expectedLinks) {
			Assert.assertNotNull(links.getLinkForRel(expectedLink));
		}
	}

}