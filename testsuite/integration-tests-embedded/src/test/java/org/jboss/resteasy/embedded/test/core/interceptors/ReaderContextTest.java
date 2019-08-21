package org.jboss.resteasy.embedded.test.core.interceptors;

import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedServerTestBase;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextArrayListEntityProvider;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextFirstReaderInterceptor;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextFirstWriterInterceptor;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextLinkedListEntityProvider;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextResource;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextSecondReaderInterceptor;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextSecondWriterInterceptor;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static org.jboss.resteasy.embedded.test.TestPortProvider.generateURL;

/**
 * @tpSubChapter
 * @tpChapter Embedded Containers
 * @tpTestCaseDetails Basic test for reated context
 * @tpSince RESTEasy 4.1.0
 */
public class ReaderContextTest extends EmbeddedServerTestBase {

   public static final String readFromReader(Reader reader) throws IOException {
      BufferedReader br = new BufferedReader(reader);
      String entity = br.readLine();
      br.close();
      return entity;
   }

   static Client client;
   private static EmbeddedJaxrsServer server;

   @BeforeClass
   public static void before() throws Exception {
      server = getServer();
      ResteasyDeployment deployment = server.getDeployment();
      deployment.getScannedResourceClasses().add(ReaderContextResource.class.getName());
      deployment.getScannedProviderClasses().add(ReaderContextArrayListEntityProvider.class.getName());
      deployment.getScannedProviderClasses().add(ReaderContextLinkedListEntityProvider.class.getName());
      deployment.getScannedProviderClasses().add(ReaderContextFirstReaderInterceptor.class.getName());
      deployment.getScannedProviderClasses().add(ReaderContextFirstWriterInterceptor.class.getName());
      deployment.getScannedProviderClasses().add(ReaderContextSecondReaderInterceptor.class.getName());
      deployment.getScannedProviderClasses().add(ReaderContextSecondWriterInterceptor.class.getName());
      server.start();
      server.deploy();
   }

   @AfterClass
   public static void cleanup() {
      server.stop();
   }

   /**
    * @tpTestDetails Check post request.
    * @tpSince RESTEasy 4.1.0
    */
   @Test
   public void readerContextOnClientTest() {
      client = ClientBuilder.newClient();

      WebTarget target = client.target(generateURL("/resource/poststring"));
      target.register(ReaderContextFirstReaderInterceptor.class);
      target.register(ReaderContextSecondReaderInterceptor.class);
      target.register(ReaderContextArrayListEntityProvider.class);
      target.register(ReaderContextLinkedListEntityProvider.class);
      Response response = target.request().post(Entity.text("plaintext"));
      response.getHeaders().add(ReaderContextResource.HEADERNAME,
            ReaderContextFirstReaderInterceptor.class.getName());
      @SuppressWarnings("unchecked")
      List<String> list = response.readEntity(List.class);
      Assert.assertTrue("Returned list in not instance of ArrayList", ArrayList.class.isInstance(list));
      String entity = list.get(0);
      Assert.assertTrue("Wrong interceptor type in response", entity.contains(ReaderContextSecondReaderInterceptor.class.getName()));
      Assert.assertTrue("Wrong interceptor annotation in response", entity.contains(ReaderContextSecondReaderInterceptor.class.getAnnotations()[0]
            .annotationType().getName()));

      client.close();
   }
}
