package org.jboss.resteasy.test.microprofile.config;

import java.util.PropertyPermission;

import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.MicroProfileDependent;
import org.jboss.resteasy.test.microprofile.config.resource.ResteasyConfigResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Tests ResteasyConfig in the absence of MicroProfile Config facility
 *               when Resteasy is initialized as a servlet.
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression tests for RESTEASY-2406
 * @tpSince RESTEasy 3.12.0
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category(MicroProfileDependent.class)
public class ResteasyConfigServletTest extends ResteasyConfigTestParent
{
   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(ResteasyConfigTestParent.class.getSimpleName())
            .setWebXML(ResteasyConfigServletTest.class.getPackage(), "web_servlet.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
            new PropertyPermission("system", "write")
            ), "permissions.xml");
      return TestUtil.finishContainerPrepare(war, null, ResteasyConfigResource.class);
   }

   /**
    * @tpTestDetails Verify ServletBootstrap.getParameter() gets ServletConfig init-param when available.
    * @tpSince RESTEasy 3.12.0
    */
   @Test
   public void testServletBootstrapInit() throws Exception {
      Response response = client.target(generateURL("/bootstrap/init")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("init-init", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Verify ServletBootstrap.getParameter() gets ServletContext context-param
    *                when ServletConfig init-param is unavailable and ServletContext context-param is available.
    * @tpSince RESTEasy 3.12.0
    */
   @Test
   public void testServletBootstrapContext() throws Exception {
      Response response = client.target(generateURL("/bootstrap/context")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("context-context", response.readEntity(String.class));
   }
}
