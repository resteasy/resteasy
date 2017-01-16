package org.jboss.resteasy.links.test;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.embedded.SimplePrincipal;
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
public class TestSecureLinks
{
   private static NettyJaxrsServer server;
   private static Dispatcher dispatcher;

   @BeforeClass
   public static void beforeClass() throws Exception
   {
      server = new NettyJaxrsServer();
      server.setPort(TestPortProvider.getPort());
      server.setRootResourcePath("/");
      server.setSecurityDomain(new SecurityDomain()
      {

         public Principal authenticate(String username, String password) throws SecurityException
         {
            return new SimplePrincipal(username);
         }

         public boolean isUserInRole(Principal username, String role)
         {
            return username.getName().equals(role);
         }

      });

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
		return Arrays.asList(new Class<?>[]{SecureBookStore.class}, new Class<?>[]{SecureBookStoreMinimal.class});
	}

	private Class<?> resourceType;
	private String url;
	private BookStoreService client;
	private DefaultHttpClient httpClient;
	public TestSecureLinks(Class<?> resourceType){
		this.resourceType = resourceType;
	}

	@Before
	public void before(){
		POJOResourceFactory noDefaults = new POJOResourceFactory(resourceType);
		dispatcher.getRegistry().addResourceFactory(noDefaults);
		url = generateBaseUrl();
		
		// Configure HttpClient to authenticate preemptively
		// by prepopulating the authentication data cache.
		// 1. Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();
		// 2. Generate BASIC scheme object and add it to the local auth cache
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(getHttpHost(url), basicAuth);
		// 3. Add AuthCache to the execution context
		BasicHttpContext localContext = new BasicHttpContext();
		localContext.setAttribute(ClientContext.AUTH_CACHE, authCache);
		
		httpClient = new DefaultHttpClient();
		ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(httpClient, localContext);
		client = ProxyFactory.create(BookStoreService.class, url, executor);
	}

	@After
	public void after(){
		dispatcher.getRegistry().removeRegistrations(resourceType);
	}
	
	@Test
	public void testSecureLinksAdmin() throws Exception
	{
		CredentialsProvider cp = httpClient.getCredentialsProvider();
		cp.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("admin", "asd"));
		Book book = client.getBookXML("foo");
		checkBookLinks1(url, book, "add", "update", "list", "self", "remove");
	}

	@Test
	public void testSecureLinksPowerUser() throws Exception
	{
		CredentialsProvider cp = httpClient.getCredentialsProvider();
		cp.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("power-user", "asd"));
		Book book = client.getBookXML("foo");
		checkBookLinks1(url, book, "add", "update", "list", "self");
	}

	@Test
	public void testSecureLinksUser() throws Exception
	{
		CredentialsProvider cp = httpClient.getCredentialsProvider();
		cp.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("user", "asd"));
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

	private HttpHost getHttpHost(String url)
	{
		int i1 = url.indexOf(':');
		String scheme = url.substring(0, i1);
		int i2 = url.indexOf(':', i1 + 1);
		String host = url.substring(i1 + 3, i2);
		int i3 = url.indexOf('/', i2 + 1);
		String port = i3 == -1 ? url.substring(i2 + 1)
				               : url.substring(i2 + 1, i3); 
		return new HttpHost(host, Integer.valueOf(port), scheme);
	}
}
