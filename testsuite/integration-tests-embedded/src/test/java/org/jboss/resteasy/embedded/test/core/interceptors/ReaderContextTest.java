package org.jboss.resteasy.embedded.test.core.interceptors;

import org.jboss.resteasy.embedded.test.AbstractBootstrapTest;
import org.jboss.resteasy.embedded.test.TestApplication;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextArrayListEntityProvider;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextFirstReaderInterceptor;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextFirstWriterInterceptor;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextLinkedListEntityProvider;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextResource;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextSecondReaderInterceptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @tpSubChapter
 * @tpChapter Embedded Containers
 * @tpTestCaseDetails Basic test for reated context
 * @tpSince RESTEasy 4.1.0
 */
public class ReaderContextTest extends AbstractBootstrapTest {

   @Before
   public void before() throws Exception {
      start(new TestApplication(ReaderContextResource.class,
              ReaderContextArrayListEntityProvider.class,
              ReaderContextLinkedListEntityProvider.class,
              ReaderContextFirstReaderInterceptor.class,
              ReaderContextFirstWriterInterceptor.class,
              ReaderContextSecondReaderInterceptor.class,
              ReaderContextSecondReaderInterceptor.class));
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
