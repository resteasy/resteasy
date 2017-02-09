package org.jboss.resteasy.test.client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.RxInvoker;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.dmr.ModelNode;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
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
   private static final TestExecutor EXECUTOR = new TestExecutor(5, 5, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
   
   private static ModelNode origDisallowedMethodsValue;
   private static Client client;

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
       
       client = ClientBuilder.newClient();
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
       
       client.close();
   }
   
   public static class TestExecutor extends ThreadPoolExecutor
   {
      public volatile boolean used;
      
      public TestExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue)
      {
         super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
      }
      
      public void execute(Runnable command)
      {
         used = true;
         super.execute(command);
      }
   }
   
   public static class TestRxInvoker extends CompletionStageRxInvokerImpl
   {
      public static volatile boolean used;
      
      public TestRxInvoker()
      {
         used = true;
      }
      
      public TestRxInvoker(ExecutorService executor)
      {
         super(executor);
         used = true;
      }
      
      public TestRxInvoker(ClientInvocationBuilder builder)
      {
         super(builder);
         used = true;
      }
      
      public TestRxInvoker(ClientInvocationBuilder builder, ExecutorService executor)
      {
         super(builder, executor);
         used = true;
      }
   }

   static RxInvoker<?> buildInvoker(Builder builder, boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      if (useCustomInvoker)
      {
         if (useExecutor)
         {
            return builder.rx(TestRxInvoker.class, EXECUTOR).builder(builder);
         }
         else
         {
            return builder.rx(TestRxInvoker.class).builder(builder);
         }
      }
      else
      {
         if (useExecutor)
         {
            return builder.rx(EXECUTOR);
         }
         else
         {
            return builder.rx();
         }
      }
   }
   
   public static void reset()
   {
      EXECUTOR.used = false;
      TestRxInvoker.used = false;
   }

   @Test
   public void TestRxClientGet() throws Exception
   {
      doTestRxClientGet(false, false);
      doTestRxClientGet(false, true);
      doTestRxClientGet(true, false);
      doTestRxClientGet(true, true);
   }
   
   void doTestRxClientGet(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/get")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.get();
      String response = cs.get().readEntity(String.class);
      Assert.assertEquals("get", response);
      Assert.assertEquals(useCustomInvoker, TestRxInvoker.used);
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }

   @Test
   public void testRxClientGetClass() throws Exception
   {
      doTestRxClientGetClass(false, false);
      doTestRxClientGetClass(false, true);
      doTestRxClientGetClass(true, false);
      doTestRxClientGetClass(true, true);
   }
   
   void doTestRxClientGetClass(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/get")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.get(String.class);
      Assert.assertEquals("get", cs.get());
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }
   
   @Test
   public void testRxClientGetGenericType() throws Exception
   {
      doTestRxClientGetGenericType(false, false);
      doTestRxClientGetGenericType(false, true);
      doTestRxClientGetGenericType(true, false);
      doTestRxClientGetGenericType(true, true);
   }
   
   void doTestRxClientGetGenericType(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/get")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.get(STRING_TYPE);
      Assert.assertEquals("get", cs.get());
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }
   
   @Test
   public void testRxClientPut() throws Exception
   {
      doTestRxClientPut(false, false);
      doTestRxClientPut(false, true);
      doTestRxClientPut(true, false);
      doTestRxClientPut(true, true);
   }
   
   void doTestRxClientPut(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/put")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.put(Entity.entity("put", MediaType.TEXT_PLAIN_TYPE));
      String response = cs.get().readEntity(String.class);
      Assert.assertEquals("put", response);
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }
   
   @Test
   public void testRxClientPutClass() throws Exception
   {
      doTestRxClientPutClass(false, false);
      doTestRxClientPutClass(false, true);
      doTestRxClientPutClass(true, false);
      doTestRxClientPutClass(true, true);
   }
   
   void doTestRxClientPutClass(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {   
      reset();
      Builder builder = client.target(generateURL("/put")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.put(Entity.entity("put", MediaType.TEXT_PLAIN_TYPE), String.class);
      String response = cs.get();
      Assert.assertEquals("put", response);
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }
   
   @Test
   public void testRxClientPutGenericType() throws Exception
   {
      doTestRxClientPutGenericType(false, false);
      doTestRxClientPutGenericType(false, true);
      doTestRxClientPutGenericType(true, false);
      doTestRxClientPutGenericType(true, true);
   }
   
   void doTestRxClientPutGenericType(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/put")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.put(Entity.entity("put", MediaType.TEXT_PLAIN_TYPE), STRING_TYPE);
      String response = cs.get();
      Assert.assertEquals("put", response);
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }
   
   @Test
   public void testRxClientPost() throws Exception
   {
      doTestRxClientPost(false, false);
      doTestRxClientPost(false, true);
      doTestRxClientPost(true, false);
      doTestRxClientPost(true, true);
   }
   
   void doTestRxClientPost(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/post")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.post(Entity.entity("post", MediaType.TEXT_PLAIN_TYPE));
      String response = cs.get().readEntity(String.class);
      Assert.assertEquals("post", response);
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }
   
   @Test
   public void testRxClientPostClass() throws Exception
   {
      doTestRxClientPostClass(false, false);
      doTestRxClientPostClass(false, true);
      doTestRxClientPostClass(true, false);
      doTestRxClientPostClass(true, true);
   }

   void doTestRxClientPostClass(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/post")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.post(Entity.entity("post", MediaType.TEXT_PLAIN_TYPE), String.class);
      String response = cs.get();
      Assert.assertEquals("post", response);
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }
   
   @Test
   public void testRxClientPostGenericType() throws Exception
   {
      dotestRxClientPostGenericType(false, false);
      dotestRxClientPostGenericType(false, true);
      dotestRxClientPostGenericType(true, false);
      dotestRxClientPostGenericType(true, true);
   }
   
   void dotestRxClientPostGenericType(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/post")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.post(Entity.entity("post", MediaType.TEXT_PLAIN_TYPE), STRING_TYPE);
      String response = cs.get();
      Assert.assertEquals("post", response);
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }
   
   @Test
   public void testRxClientDelete() throws Exception
   {
      doTestRxClientDelete(false, false);
      doTestRxClientDelete(false, true);
      doTestRxClientDelete(true, false);
      doTestRxClientDelete(true, true);
   }
   
   void doTestRxClientDelete(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/delete")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.delete();
      String response = cs.get().readEntity(String.class);
      Assert.assertEquals("delete", response);
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }

   @Test
   public void testRxClientDeleteClass() throws Exception
   {
      doTestRxClientDeleteClass(false, false);
      doTestRxClientDeleteClass(false, true);
      doTestRxClientDeleteClass(true, false);
      doTestRxClientDeleteClass(true, true);
   }
   
   void doTestRxClientDeleteClass(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/delete")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.delete(String.class);
      Assert.assertEquals("delete", cs.get());
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }
   
   @Test
   public void testRxClientDeleteGenericType() throws Exception
   {
      doTestRxClientDeleteGenericType(false, false);
      doTestRxClientDeleteGenericType(false, true);
      doTestRxClientDeleteGenericType(true, false);
      doTestRxClientDeleteGenericType(true, true);
   }
   
   void doTestRxClientDeleteGenericType(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/delete")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.delete(STRING_TYPE);
      Assert.assertEquals("delete", cs.get());
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }
   
   @Test
   public void testRxClientHead() throws Exception
   {
      doTestRxClientHead(false, false);
      doTestRxClientHead(false, true);
      doTestRxClientHead(true, false);
      doTestRxClientHead(true, true);
   }
   
   void doTestRxClientHead(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/head")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.head();
      Response response = cs.get();
      Assert.assertEquals(204, response.getStatus());
      Assert.assertEquals("head", response.getStringHeaders().getFirst("key"));
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }
   
   @Test
   public void testRxClientOptions() throws Exception
   {
      doTestRxClientOptions(false, false);
      doTestRxClientOptions(false, true);
      doTestRxClientOptions(true, false);
      doTestRxClientOptions(true, true);
   }
   
   void doTestRxClientOptions(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/options")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.options();
      String response = cs.get().readEntity(String.class);
      Assert.assertEquals("options", response);
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }

   @Test
   public void testRxClientOptionsClass() throws Exception
   {
      doTestRxClientOptionsClass(false, false);
      doTestRxClientOptionsClass(false, true);
      doTestRxClientOptionsClass(true, false);
      doTestRxClientOptionsClass(true, true);
   }
   
   void doTestRxClientOptionsClass(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/options")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.options(String.class);
      Assert.assertEquals("options", cs.get());
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }
   
   @Test
   public void testRxClientOptionsGenericType() throws Exception
   {
      doTestRxClientOptionsGenericType(false, false);
      doTestRxClientOptionsGenericType(false, true);
      doTestRxClientOptionsGenericType(true, false);
      doTestRxClientOptionsGenericType(true, true);
   }
   
   void doTestRxClientOptionsGenericType(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/options")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.options(STRING_TYPE);
      Assert.assertEquals("options", cs.get());
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }
   
   @Test
   public void testRxClientTrace() throws Exception
   {
      doTestRxClientTrace(false, false);
      doTestRxClientTrace(false, true);
      doTestRxClientTrace(true, false);
      doTestRxClientTrace(true, true);
   }
   
   void doTestRxClientTrace(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/trace")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.trace();
      String response = cs.get().readEntity(String.class);
      Assert.assertEquals("trace", response);
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }

   @Test
   public void testRxClientTraceClass() throws Exception
   {
      doTestRxClientTraceClass(false, false);
      doTestRxClientTraceClass(false, true);
      doTestRxClientTraceClass(true, false);
      doTestRxClientTraceClass(true, true);
   }
   
   void doTestRxClientTraceClass(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/trace")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.trace(String.class);
      Assert.assertEquals("trace", cs.get());
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }
   
   @Test
   public void testRxClientTraceGenericType() throws Exception
   {
      reset();
      doTestRxClientTraceGenericType(false, false);
      doTestRxClientTraceGenericType(false, true);
      doTestRxClientTraceGenericType(true, false);
      doTestRxClientTraceGenericType(true, true);
   }
   
   void doTestRxClientTraceGenericType(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/trace")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.trace(STRING_TYPE);
      Assert.assertEquals("trace", cs.get());
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }
   
   @Test
   public void testRxClientMethod() throws Exception
   {
      doTestRxClientMethod(false, false);
      doTestRxClientMethod(false, true);
      doTestRxClientMethod(true, false);
      doTestRxClientMethod(true, true);
   }
   
   void doTestRxClientMethod(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/method")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.method("METHOD");
      String response = cs.get().readEntity(String.class);
      Assert.assertEquals("method", response);
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }

   @Test
   public void testRxClientMethodClass() throws Exception
   {
      doTestRxClientMethodClass(false, false);
      doTestRxClientMethodClass(false, true);
      doTestRxClientMethodClass(true, false);
      doTestRxClientMethodClass(true, true);
   }
   
   void doTestRxClientMethodClass(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/method")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.method("METHOD", String.class);
      Assert.assertEquals("method", cs.get());
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }
   
   @Test
   public void testRxClientMethodGenericType() throws Exception
   {
      doTestRxClientMethodGenericType(false, false);
      doTestRxClientMethodGenericType(false, true);
      doTestRxClientMethodGenericType(true, false);
      doTestRxClientMethodGenericType(true, true);
   }
   
   void doTestRxClientMethodGenericType(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/method")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.method("METHOD", STRING_TYPE);
      Assert.assertEquals("method", cs.get());
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }
   
   @Test
   public void testRxClientMethodEntity() throws Exception
   {
      doTestRxClientMethodEntity(false, false);
      doTestRxClientMethodEntity(false, true);
      doTestRxClientMethodEntity(true, false);
      doTestRxClientMethodEntity(true, true);
   }
   
   void doTestRxClientMethodEntity(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/methodEntity")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.method("METHOD", Entity.entity("methodEntity", MediaType.TEXT_PLAIN_TYPE));
      String response = cs.get().readEntity(String.class);
      Assert.assertEquals("methodEntity", response);
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }
   
   @Test
   public void testRxClientMethodClassEntity() throws Exception
   {
      doTestRxClientMethodClassEntity(false, false);
      doTestRxClientMethodClassEntity(false, true);
      doTestRxClientMethodClassEntity(true, false);
      doTestRxClientMethodClassEntity(true, true);
   }
   
   void doTestRxClientMethodClassEntity(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/methodEntity")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.method("METHOD", Entity.entity("methodEntity", MediaType.TEXT_PLAIN_TYPE), String.class);
      String response = cs.get();
      Assert.assertEquals("methodEntity", response);
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }
   
   @Test
   public void testRxClientMethodGenericTypeEntity() throws Exception
   {
      doTestRxClientMethodGenericTypeEntity(false, false);
      doTestRxClientMethodGenericTypeEntity(false, true);
      doTestRxClientMethodGenericTypeEntity(true, false);
      doTestRxClientMethodGenericTypeEntity(true, true);
   }
   
   void doTestRxClientMethodGenericTypeEntity(boolean useCustomInvoker, boolean useExecutor) throws Exception
   {
      reset();
      Builder builder = client.target(generateURL("/methodEntity")).request();
      RxInvoker<?> invoker = buildInvoker(builder, useCustomInvoker, useExecutor);
      CompletableFuture<String> cs = (CompletableFuture<String>) invoker.method("METHOD", Entity.entity("methodEntity", MediaType.TEXT_PLAIN_TYPE), STRING_TYPE);
      String response = cs.get();
      Assert.assertEquals("methodEntity", response);
      Assert.assertEquals(useExecutor, EXECUTOR.used);
      Assert.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
   }
}

