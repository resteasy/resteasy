package org.jboss.resteasy.test.interceptor;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.interceptor.resource.InterceptorStreamResource;
import org.jboss.resteasy.test.interceptor.resource.TestInterceptor;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.lang.reflect.ReflectPermission;
import java.net.SocketPermission;
import java.util.PropertyPermission;

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.1.2
 * @tpTestCaseDetails Verify outpustream close is invoked on server side (https://issues.jboss.org/browse/RESTEASY-1650)
 */
@RunWith(Arquillian.class)
public class StreamCloseTest
{
   @Deployment
   public static Archive<?> deploy()
   {
      WebArchive war = TestUtil.prepareArchive(StreamCloseTest.class.getSimpleName());
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
              new SocketPermission(PortProviderUtil.getHost(), "connect,resolve"),
              new PropertyPermission("arquillian.*", "read"),
              new RuntimePermission("accessDeclaredMembers"),
              new ReflectPermission("suppressAccessChecks"),
              new PropertyPermission("org.jboss.resteasy.port", "read"),
              new RuntimePermission("getenv.RESTEASY_PORT"),
              new PropertyPermission("ipv6", "read"),
              new PropertyPermission("node", "read")
      ), "permissions.xml");
      return TestUtil.finishContainerPrepare(war, null, InterceptorStreamResource.class, TestInterceptor.class, PortProviderUtil.class);
   }

   static Client client;

   @Before
   public void setup()
   {
      client = ClientBuilder.newClient();
   }

   @After
   public void cleanup()
   {
      client.close();
   }

   private String generateURL(String path)
   {
      return PortProviderUtil.generateURL(path, StreamCloseTest.class.getSimpleName());
   }

   @Test
   public void testPriority() throws Exception
   {
      final int count = TestInterceptor.closeCounter.get();
      Response response = client.target(generateURL("/test")).request().post(Entity.text("test"));
      response.bufferEntity();
      Assert.assertEquals("Wrong response status, interceptors don't work correctly", HttpResponseCodes.SC_OK,
            response.getStatus());
      Assert.assertEquals("Wrong content of response, interceptors don't work correctly", "test",
            response.readEntity(String.class));
      response.close();
      Assert.assertEquals(1, TestInterceptor.closeCounter.get() - count);

   }
}
