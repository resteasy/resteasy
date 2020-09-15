package org.jboss.resteasy.test.cache;

import java.security.AllPermission;
import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.ExpectedFailingOnWildFly19;
import org.jboss.resteasy.category.NotForBootableJar;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.cache.server.InfinispanCache;
import org.jboss.resteasy.plugins.cache.server.ServerCache;
import org.jboss.resteasy.plugins.cache.server.ServerCacheFeature;
import org.jboss.resteasy.plugins.cache.server.ServerCacheHitFilter;
import org.jboss.resteasy.plugins.cache.server.ServerCacheInterceptor;
import org.jboss.resteasy.test.cache.resource.ServerCacheInterceptorResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * @tpSubChapter RESTEasy Cache Core
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1423
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category({
        ExpectedFailingOnWildFly19.class,
        NotForBootableJar.class // related resteasy-cache-core module is not delivered by WF, so it's not necessary to check it with bootable jar
})
public class ServerCacheInterceptorTest {

   private static ResteasyClient clientA;
   private static ResteasyClient clientB;

   @Deployment
   public static Archive<?> deploySimpleResource() {
      List<Class<?>> singletons = new ArrayList<>();
      singletons.add(ServerCacheFeature.class);
      WebArchive war = TestUtil.prepareArchive(ServerCacheInterceptorTest.class.getSimpleName());
      // This test is not supposed to run with security manager
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(new AllPermission()), "permissions.xml");
      war.addClasses(ServerCache.class, InfinispanCache.class, ServerCacheHitFilter.class, ServerCacheInterceptor.class);
      war.addAsManifestResource(new StringAsset("Manifest-Version: 1.0\n" + "Dependencies: org.infinispan\n"), "MANIFEST.MF");
      return TestUtil.finishContainerPrepare(war, null, singletons, ServerCacheInterceptorResource.class);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, ServerCacheInterceptorTest.class.getSimpleName());
   }

   @Before
   public void setup() {
      clientA = new ResteasyClientBuilder().build();
      clientB = new ResteasyClientBuilder().build();
   }

   @After
   public void after() throws Exception {
      clientA.close();
      clientB.close();
   }

   /**
    * @tpTestDetails Verifies that a 'public' resource is cached by the server side cache.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void cachePublicResource() {
      String responseA = clientA.target(generateURL("/public")).request().get(String.class);
      String responseB = clientB.target(generateURL("/public")).request().get(String.class);
      Assert.assertEquals(responseA, responseB);
   }

   /**
    * @tpTestDetails Verifies that a 'private' resource is not cached by the server side cache.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void doNotCachePrivateResource() {
      String responseA = clientA.target(generateURL("/private")).request().get(String.class);
      String responseB = clientB.target(generateURL("/private")).request().get(String.class);
      Assert.assertNotEquals(responseA, responseB);
   }

   /**
    * @tpTestDetails Verifies that a resource marked with the 'no-store' directive is not cached by the server side cache.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void doNotCacheNoStoreResource() {
      String responseA = clientA.target(generateURL("/no-store")).request().get(String.class);
      String responseB = clientB.target(generateURL("/no-store")).request().get(String.class);
      Assert.assertNotEquals(responseA, responseB);
   }

}
