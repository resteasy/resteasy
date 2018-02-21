package org.jboss.resteasy.test.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.RxInvoker;
import javax.ws.rs.client.RxInvokerProvider;
import javax.ws.rs.client.SyncInvoker;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.dmr.ModelNode;
import org.jboss.resteasy.client.jaxrs.internal.CompletionStageRxInvokerImpl;
import org.jboss.resteasy.test.client.resource.TestResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

/**
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @author <a href="mailto:alessio.soldano@jboss.com">Alessio Soldano</a>
 * @date March 9, 2016
 */
@RunWith(Arquillian.class)
@RunAsClient
public class RxInvokerTest extends ClientTestBase
{
   private static final GenericType<String> STRING_TYPE = new GenericType<String>() {};
   private static final Address ADDRESS = Address.subsystem("undertow").and("server", "default-server").and("http-listener", "default");
   
   private static ModelNode origDisallowedMethodsValue;

   @Deployment
   public static Archive<?> deploy() {
       WebArchive war = TestUtil.prepareArchive(RxInvokerTest.class.getSimpleName());
       return TestUtil.finishContainerPrepare(war, null, TestResource.class);
   }

   @BeforeClass
   public static void setup() throws Exception {
       OnlineManagementClient mgmtClient = TestUtil.clientInit();
       Administration admin = new Administration(mgmtClient);
       Operations ops = new Operations(mgmtClient);

       // get original 'disallowed methods' value
       origDisallowedMethodsValue = ops.readAttribute(ADDRESS, "disallowed-methods").value();
       // set 'disallowed methods' to empty list to allow TRACE
       ops.writeAttribute(ADDRESS, "disallowed-methods", new ModelNode().setEmptyList());

       // reload server
       admin.reload();
       mgmtClient.close();
   }

   @AfterClass
   public static void cleanup() throws Exception {
       OnlineManagementClient mgmtClient = TestUtil.clientInit();
       Administration admin = new Administration(mgmtClient);
       Operations ops = new Operations(mgmtClient);

       // write original 'disallowed methods' value
       ops.writeAttribute(ADDRESS, "disallowed-methods", origDisallowedMethodsValue);

       // reload server
       admin.reload();
       mgmtClient.close();
   }
   
   public static class TestRxInvokerProvider implements RxInvokerProvider<TestRxInvoker>
   {

      @Override
      public boolean isProviderFor(Class<?> clazz)
      {
         return clazz.isAssignableFrom(TestRxInvoker.class);
      }

      @Override
      public TestRxInvoker getRxInvoker(SyncInvoker syncInvoker, ExecutorService executorService)
      {
         return new TestRxInvoker(syncInvoker, executorService);
      }

   }
   
   public static class TestRxInvoker extends CompletionStageRxInvokerImpl
   {
      public static volatile boolean used;
      
      public TestRxInvoker(SyncInvoker builder)
      {
         super(builder);
         used = true;
      }
      
      public TestRxInvoker(SyncInvoker builder, ExecutorService executor)
      {
         super(builder, executor);
         used = true;
      }
   }
   
   static Client newClient(boolean useCustomInvoker) {
      TestRxInvoker.used = false;
      final Client client;
      if (useCustomInvoker) {
         client = ClientBuilder.newClient().register(TestRxInvokerProvider.class, RxInvokerProvider.class);
      } else {
         client = ClientBuilder.newClient();
      }
      return client;
   }

   @Test
   public void testRxClientGet() throws Exception
   {
      doTestRxClientGet(false);
      doTestRxClientGet(true);
   }
   
   void doTestRxClientGet(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/get")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.get();
      String response = cs.get().readEntity(String.class);
      Assert.assertEquals("get", response);
      Assert.assertEquals(useCustomInvoker, TestRxInvoker.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }

   @Test
   public void testRxClientGetClass() throws Exception
   {
      doTestRxClientGetClass(false);
      doTestRxClientGetClass(true);
   }
   
   void doTestRxClientGetClass(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/get")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.get(String.class);
      Assert.assertEquals("get", cs.get());
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }
   
   @Test
   public void testRxClientGetGenericType() throws Exception
   {
      doTestRxClientGetGenericType(false);
      doTestRxClientGetGenericType(true);
   }
   
   void doTestRxClientGetGenericType(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/get")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.get(STRING_TYPE);
      Assert.assertEquals("get", cs.get());
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }
   
   @Test
   public void testRxClientPut() throws Exception
   {
      doTestRxClientPut(false);
      doTestRxClientPut(true);
   }
   
   void doTestRxClientPut(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/put")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.put(Entity.entity("put", MediaType.TEXT_PLAIN_TYPE));
      String response = cs.get().readEntity(String.class);
      Assert.assertEquals("put", response);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }
   
   @Test
   public void testRxClientPutClass() throws Exception
   {
      doTestRxClientPutClass(false);
      doTestRxClientPutClass(true);
   }
   
   void doTestRxClientPutClass(boolean useCustomInvoker) throws Exception
   {   
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/put")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.put(Entity.entity("put", MediaType.TEXT_PLAIN_TYPE), String.class);
      String response = cs.get();
      Assert.assertEquals("put", response);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }
   
   @Test
   public void testRxClientPutGenericType() throws Exception
   {
      doTestRxClientPutGenericType(false);
      doTestRxClientPutGenericType(true);
   }
   
   void doTestRxClientPutGenericType(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/put")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.put(Entity.entity("put", MediaType.TEXT_PLAIN_TYPE), STRING_TYPE);
      String response = cs.get();
      Assert.assertEquals("put", response);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }
   
   @Test
   public void testRxClientPost() throws Exception
   {
      doTestRxClientPost(false);
      doTestRxClientPost(true);
   }
   
   void doTestRxClientPost(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/post")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.post(Entity.entity("post", MediaType.TEXT_PLAIN_TYPE));
      String response = cs.get().readEntity(String.class);
      Assert.assertEquals("post", response);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }
   
   @Test
   public void testRxClientPostClass() throws Exception
   {
      doTestRxClientPostClass(false);
      doTestRxClientPostClass(true);
   }

   void doTestRxClientPostClass(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/post")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.post(Entity.entity("post", MediaType.TEXT_PLAIN_TYPE), String.class);
      String response = cs.get();
      Assert.assertEquals("post", response);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }
   
   @Test
   public void testRxClientPostGenericType() throws Exception
   {
      dotestRxClientPostGenericType(false);
      dotestRxClientPostGenericType(true);
   }
   
   void dotestRxClientPostGenericType(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/post")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.post(Entity.entity("post", MediaType.TEXT_PLAIN_TYPE), STRING_TYPE);
      String response = cs.get();
      Assert.assertEquals("post", response);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }
   
   @Test
   public void testRxClientDelete() throws Exception
   {
      doTestRxClientDelete(false);
      doTestRxClientDelete(true);
   }
   
   void doTestRxClientDelete(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/delete")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.delete();
      String response = cs.get().readEntity(String.class);
      Assert.assertEquals("delete", response);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }

   @Test
   public void testRxClientDeleteClass() throws Exception
   {
      doTestRxClientDeleteClass(false);
      doTestRxClientDeleteClass(true);
   }
   
   void doTestRxClientDeleteClass(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/delete")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.delete(String.class);
      Assert.assertEquals("delete", cs.get());
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }
   
   @Test
   public void testRxClientDeleteGenericType() throws Exception
   {
      doTestRxClientDeleteGenericType(false);
      doTestRxClientDeleteGenericType(true);
   }
   
   void doTestRxClientDeleteGenericType(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/delete")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.delete(STRING_TYPE);
      Assert.assertEquals("delete", cs.get());
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }
   
   @Test
   public void testRxClientHead() throws Exception
   {
      doTestRxClientHead(false);
      doTestRxClientHead(true);
   }
   
   void doTestRxClientHead(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/head")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.head();
      Response response = cs.get();
      Assert.assertEquals(204, response.getStatus());
      Assert.assertEquals("head", response.getStringHeaders().getFirst("key"));
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }
   
   @Test
   public void testRxClientOptions() throws Exception
   {
      doTestRxClientOptions(false);
      doTestRxClientOptions(true);
   }
   
   void doTestRxClientOptions(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/options")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.options();
      String response = cs.get().readEntity(String.class);
      Assert.assertEquals("options", response);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }

   @Test
   public void testRxClientOptionsClass() throws Exception
   {
      doTestRxClientOptionsClass(false);
      doTestRxClientOptionsClass(true);
   }
   
   void doTestRxClientOptionsClass(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/options")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.options(String.class);
      Assert.assertEquals("options", cs.get());
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }
   
   @Test
   public void testRxClientOptionsGenericType() throws Exception
   {
      doTestRxClientOptionsGenericType(false);
      doTestRxClientOptionsGenericType(true);
   }
   
   void doTestRxClientOptionsGenericType(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/options")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.options(STRING_TYPE);
      Assert.assertEquals("options", cs.get());
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }
   
   @Test
   public void testRxClientTrace() throws Exception
   {
      doTestRxClientTrace(false);
      doTestRxClientTrace(true);
   }
   
   void doTestRxClientTrace(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/trace")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.trace();
      String response = cs.get().readEntity(String.class);
      Assert.assertEquals("trace", response);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }

   @Test
   public void testRxClientTraceClass() throws Exception
   {
      doTestRxClientTraceClass(false);
      doTestRxClientTraceClass(true);
   }
   
   void doTestRxClientTraceClass(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/trace")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.trace(String.class);
      Assert.assertEquals("trace", cs.get());
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }
   
   @Test
   public void testRxClientTraceGenericType() throws Exception
   {
      doTestRxClientTraceGenericType(false);
      doTestRxClientTraceGenericType(true);
   }
   
   void doTestRxClientTraceGenericType(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/trace")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.trace(STRING_TYPE);
      Assert.assertEquals("trace", cs.get());
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }
   
   @Test
   public void testRxClientMethod() throws Exception
   {
      doTestRxClientMethod(false);
      doTestRxClientMethod(true);
   }
   
   void doTestRxClientMethod(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/method")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.method("METHOD");
      String response = cs.get().readEntity(String.class);
      Assert.assertEquals("method", response);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }

   @Test
   public void testRxClientMethodClass() throws Exception
   {
      doTestRxClientMethodClass(false);
      doTestRxClientMethodClass(true);
   }
   
   void doTestRxClientMethodClass(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/method")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.method("METHOD", String.class);
      Assert.assertEquals("method", cs.get());
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }
   
   @Test
   public void testRxClientMethodGenericType() throws Exception
   {
      doTestRxClientMethodGenericType(false);
      doTestRxClientMethodGenericType(true);
   }
   
   void doTestRxClientMethodGenericType(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/method")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.method("METHOD", STRING_TYPE);
      Assert.assertEquals("method", cs.get());
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }
   
   @Test
   public void testRxClientMethodEntity() throws Exception
   {
      doTestRxClientMethodEntity(false);
      doTestRxClientMethodEntity(true);
   }
   
   void doTestRxClientMethodEntity(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/methodEntity")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.method("METHOD", Entity.entity("methodEntity", MediaType.TEXT_PLAIN_TYPE));
      String response = cs.get().readEntity(String.class);
      Assert.assertEquals("methodEntity", response);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }
   
   @Test
   public void testRxClientMethodClassEntity() throws Exception
   {
      doTestRxClientMethodClassEntity(false);
      doTestRxClientMethodClassEntity(true);
   }
   
   void doTestRxClientMethodClassEntity(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/methodEntity")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.method("METHOD", Entity.entity("methodEntity", MediaType.TEXT_PLAIN_TYPE), String.class);
      String response = cs.get();
      Assert.assertEquals("methodEntity", response);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }

   @Test
   public void testRxClientMethodGenericTypeEntity() throws Exception
   {
      doTestRxClientMethodGenericTypeEntity(false);
      doTestRxClientMethodGenericTypeEntity(true);
   }

   void doTestRxClientMethodGenericTypeEntity(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/methodEntity")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.method("METHOD", Entity.entity("methodEntity", MediaType.TEXT_PLAIN_TYPE), STRING_TYPE);
      String response = cs.get();
      Assert.assertEquals("methodEntity", response);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }

   /**
    * @tpTestDetails end-point method returns String data after some delay (3s)
    *                client use RxInvoker. Data should not be prepared right after CompletionStage object are returned from client
    *                CompletionStage should return correct data after 3s delay
    * @tpSince RESTEasy 3.5
    */
   @Test
   public void testGetDataWithDelay() throws Exception
   {
      doTestGetDataWithDelay(false);
      doTestGetDataWithDelay(true);
   }

   void doTestGetDataWithDelay(boolean useCustomInvoker) throws Exception
   {
      final Client client = newClient(useCustomInvoker);
      Builder builder = client.target(generateURL("/sleep")).request();
      RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
      Future<String> future = ((CompletableFuture<String>) invoker.get(String.class)).toCompletableFuture();
      Assert.assertFalse(future.isDone());
      String response = future.get();
      Assert.assertEquals("get", response);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
      client.close();
   }
}

