package org.jboss.resteasy.test.microprofile.config;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.microprofile.config.FilterConfigSource;
import org.jboss.resteasy.microprofile.config.ServletContextConfigSource;
import org.jboss.resteasy.test.microprofile.config.resource.MicroProfileConfigFilter;
import org.jboss.resteasy.test.microprofile.config.resource.MicroProfileConfigResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;
import java.util.PropertyPermission;

/**
 * @tpSubChapter MicroProfile Config
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.6.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ConfigSourceDefaultOrdinalFilterTest {

   static ResteasyClient client;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(ConfigSourceDefaultOrdinalFilterTest.class.getSimpleName())
            .addClass(MicroProfileConfigFilter.class)
            .setWebXML(ConfigSourceDefaultOrdinalFilterTest.class.getPackage(), "web_default_ordinal_filter.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
              new PropertyPermission("system", "write")
      ), "permissions.xml");
      return TestUtil.finishContainerPrepare(war, null, MicroProfileConfigResource.class);
   }

   @BeforeClass
   public static void before() throws Exception {
      client = (ResteasyClient)ClientBuilder.newClient();
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, ConfigSourceDefaultOrdinalFilterTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Verify all built in ConfigSources ordinal for Config retrieved programmatically.
    * @tpSince RESTEasy 4.6.0
    */
   @Test
   public void testBuiltInConfigSourcesOrdinalProgrammatically() throws Exception
   {
      Map<String, Integer> builtInConfigSourceOrdinals = client.target(generateURL("/configSources/ordinal"))
            .request(MediaType.APPLICATION_JSON)
            .get(new GenericType<Map<String, Integer>>() {});

      checkBuiltInConfigSourcesOrdinal(builtInConfigSourceOrdinals);
   }

   /**
    * @tpTestDetails Verify all built in ConfigSources ordinal for Config retrieved by injection.
    * @tpSince RESTEasy 4.6.0
    */
   @Test
   public void testBuiltInConfigSourcesOrdinalInjected() throws Exception
   {
      Map<String, Integer> builtInConfigSourceOrdinals = client.target(generateURL("/configSources/ordinal"))
            .queryParam("inject", Boolean.TRUE)
            .request(MediaType.APPLICATION_JSON)
            .get(new GenericType<Map<String, Integer>>() {});

      checkBuiltInConfigSourcesOrdinal(builtInConfigSourceOrdinals);
   }

   private void checkBuiltInConfigSourcesOrdinal(Map<String, Integer> builtInConfigSourcesOrdinal)
   {
      Integer filterConfigSourceDefaultOrdinal = (Integer) FilterConfigSource.BUILT_IN_DEFAULT_ORDINAL;
      Integer servletContextConfigSourceDefaultOrdinal = (Integer) ServletContextConfigSource.BUILT_IN_DEFAULT_ORDINAL;

      Assert.assertEquals(filterConfigSourceDefaultOrdinal, builtInConfigSourcesOrdinal.get(FilterConfigSource.class.getName()));
      Assert.assertEquals(servletContextConfigSourceDefaultOrdinal, builtInConfigSourcesOrdinal.get(ServletContextConfigSource.class.getName()));
   }

}
