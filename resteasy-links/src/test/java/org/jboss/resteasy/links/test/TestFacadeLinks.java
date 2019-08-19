package org.jboss.resteasy.links.test;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.ClientBuilder;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClientEngine;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.links.RESTServiceDiscovery.AtomLink;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
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
public class TestFacadeLinks
{
   private static NettyJaxrsServer server;
   private static Dispatcher dispatcher;

   @BeforeClass
   public static void beforeClass() throws Exception
   {
      server = new NettyJaxrsServer();
      server.setPort(TestPortProvider.getPort());
      server.setRootResourcePath("/");
      ResteasyDeployment deployment = server.getDeployment();
      deployment.start();
      dispatcher = deployment.getDispatcher();
      server.start();
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
   private CloseableHttpClient httpClient;

   public TestFacadeLinks(final Class<?> resourceType){
      this.resourceType = resourceType;
   }

   @Before
   public void before(){
      POJOResourceFactory noDefaults = new POJOResourceFactory(new ResourceBuilder(), resourceType);
      dispatcher.getRegistry().addResourceFactory(noDefaults);
      httpClient = HttpClientBuilder.create().build();
      ApacheHttpClientEngine engine = ApacheHttpClientEngine.create(httpClient);
      url = generateBaseUrl();
      ResteasyWebTarget target = ((ResteasyClientBuilder)ClientBuilder.newBuilder()).httpEngine(engine).build().target(url);
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
   public void testLinksXML() throws Exception {
      ScrollableCollection comments = client.getScrollableCommentsXML("foo", "book");
      checkCommentsLinks(url, comments);
   }

   @Test
   public void testLinksJSON() throws Exception {
      ScrollableCollection comments = client.getScrollableCommentsJSON("foo", "book");
      checkCommentsLinks(url, comments);
   }

   private void checkCommentsLinks(String url, ScrollableCollection comments) {
      Assert.assertNotNull(comments);
      RESTServiceDiscovery links = comments.getRest();
      Assert.assertNotNull(links);
      Assert.assertEquals(5, links.size());
      // list
      AtomLink atomLink = links.getLinkForRel("list");
      Assert.assertNotNull(atomLink);
      Assert.assertEquals(url+"/book/foo/comments", atomLink.getHref());
      // add
      atomLink = links.getLinkForRel("add");
      Assert.assertNotNull(atomLink);
      Assert.assertEquals(url+"/book/foo/comments", atomLink.getHref());
      // comment collection
      atomLink = links.getLinkForRel("collection");
      Assert.assertNotNull(atomLink);
      Assert.assertEquals(url+"/book/foo/comment-collection", atomLink.getHref());
      // next
      atomLink = links.getLinkForRel("next");
      Assert.assertNotNull(atomLink);
      Assert.assertEquals(url+"/book/foo/comment-collection;query=book?start=1&limit=1", atomLink.getHref());
      // home
      atomLink = links.getLinkForRel("home");
      Assert.assertNotNull(atomLink);
      Assert.assertEquals(url+"/", atomLink.getHref());
   }
}
