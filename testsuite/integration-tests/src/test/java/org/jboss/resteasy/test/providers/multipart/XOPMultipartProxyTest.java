package org.jboss.resteasy.test.providers.multipart;

import java.io.ByteArrayOutputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.providers.multipart.resource.XOPMultipartProxy;
import org.jboss.resteasy.test.providers.multipart.resource.XOPMultipartProxyEngine;
import org.jboss.resteasy.test.providers.multipart.resource.XOPMultipartProxyFileProperty;
import org.jboss.resteasy.test.providers.multipart.resource.XOPMultipartProxyGenericRestResponse;
import org.jboss.resteasy.test.providers.multipart.resource.XOPMultipartProxyGetFileRequest;
import org.jboss.resteasy.test.providers.multipart.resource.XOPMultipartProxyGetFileRestResponse;
import org.jboss.resteasy.test.providers.multipart.resource.XOPMultipartProxyResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Multipart provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class XOPMultipartProxyTest {

   static Client client;

   @BeforeClass
   public static void before() throws Exception {
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
   }

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(XOPMultipartProxyTest.class.getSimpleName());
      war.addClass(XOPMultipartProxyEngine.class);
      war.addClass(XOPMultipartProxyFileProperty.class);
      war.addClass(XOPMultipartProxyGenericRestResponse.class);
      war.addClass(XOPMultipartProxyGetFileRequest.class);
      war.addClass(XOPMultipartProxyGetFileRestResponse.class);
      war.addClass(XOPMultipartProxy.class);
      return TestUtil.finishContainerPrepare(war, null, XOPMultipartProxyResource.class);
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, XOPMultipartProxyTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails MultipartFormDataOutput entity in put request with data from file is used
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testXOP() throws Exception {
      ResteasyWebTarget target = (ResteasyWebTarget) client.target(generateURL(""));
      XOPMultipartProxy test = target.proxy(XOPMultipartProxy.class);
      XOPMultipartProxyGetFileRequest req = new XOPMultipartProxyGetFileRequest();
      req.setFileName("testXOP");
      XOPMultipartProxyGetFileRestResponse fileResp = test.getFileXOPMulti(req);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      fileResp.getData().writeTo(out);
      Assert.assertEquals("testXOP", new String(out.toByteArray()));
   }
}
