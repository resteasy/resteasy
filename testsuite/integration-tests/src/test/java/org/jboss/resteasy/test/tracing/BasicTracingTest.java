package org.jboss.resteasy.test.tracing;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.resteasy.tracing.api.RESTEasyTracing;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.junit.Assert;
import org.junit.Test;

public class BasicTracingTest extends TracingTestBase {

   private static final Logger LOG = Logger.getLogger(BasicTracingTest.class);

   @Test
   @OperateOnDeployment(WAR_BASIC_TRACING_FILE)
   public void testPresencesOfServerTracingEvents() {
      String url = generateURL("/locator/foo", WAR_BASIC_TRACING_FILE);

      WebTarget base = client.target(url);

      try {
         Response response = base.request().get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());

         Map<String, Boolean> results = new HashMap<String, Boolean>();

         putTestEvents(results);

         verifyResults(response, results);

         for (String k : results.keySet()) {
            assertTrue(k + ": " + results.get(k), results.get(k));
         }

         response.close();
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }


   @Test
   @OperateOnDeployment(WAR_BASIC_TRACING_FILE)
   public void testBasic() {
//        war.as(ZipExporter.class).exportTo(new File("/tmp/" + war.getName()), true);
//        Thread.currentThread().join();
      String url = generateURL("/logger", WAR_BASIC_TRACING_FILE);
//        LOG.info("::: " + url);
//        Thread.currentThread().join();
      WebTarget base = client.target(url);
      try {
         Response response = base.request().get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         boolean hasTracing = false;
         for (Map.Entry entry : response.getStringHeaders().entrySet()) {
            if (entry.getKey().toString().startsWith(RESTEasyTracing.HEADER_TRACING_PREFIX)) {
               LOG.info("<K, V> ->" + entry);
               hasTracing = true;
               break;
            }
         }
         assertTrue(hasTracing);
         response.close();
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

}
