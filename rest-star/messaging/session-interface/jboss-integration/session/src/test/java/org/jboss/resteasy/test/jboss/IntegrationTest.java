package org.jboss.resteasy.test.jboss;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.jboss.resteasy.spi.Link;
import org.hornetq.rest.util.Constants;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class IntegrationTest
{
   @Test
   public void testCreateQueue() throws Exception
   {
      HttpClient client = new HttpClient();

      /*
      client.getState().setCredentials(
          //new AuthScope(null, 8080, "Test"),
          new AuthScope(AuthScope.ANY),
          new UsernamePasswordCredentials("guest", "guest")
      );

      client.getParams().setAuthenticationPreemptive(true);
      */

      ApacheHttpClientExecutor executor = new ApacheHttpClientExecutor(client);

      String queueConfig = "<queue name=\"testQueue\"><durable>true</durable></queue>";
      ClientRequest create = executor.createRequest("http://localhost:8080/rest-messaging/queues");
      ClientResponse cRes = create.body("application/hornetq.jms.queue+xml", queueConfig).post();
      Assert.assertEquals(201, cRes.getStatus());


      Link queue = cRes.getLocation();
      ClientRequest request = queue.request();

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = response.getHeaderAsLink("msg-create");
      System.out.println("create: " + sender);
      Link consumeNext = response.getHeaderAsLink("msg-consume-next");
      System.out.println("poller: " + consumeNext);

      ClientResponse res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      consumeNext = res.getHeaderAsLink("msg-consume-next");
      System.out.println("consumeNext: " + consumeNext);


      res = sender.request().body("text/plain", Integer.toString(2)).post();
      Assert.assertEquals(201, res.getStatus());

      System.out.println(consumeNext);
      res = consumeNext.request().header(Constants.WAIT_HEADER, "10").post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      Link session = res.getHeaderAsLink("msg-session");
      System.out.println("session: " + session);

      Assert.assertEquals(204, session.request().delete().getStatus());
      Assert.assertEquals(204, queue.request().delete().getStatus());
   }

   @Test
   public void testCreateTopic() throws Exception
   {
      HttpClient client = new HttpClient();
      /*
      client.getState().setCredentials(
          //new AuthScope(null, 8080, "Test"),
          new AuthScope(AuthScope.ANY),
          new UsernamePasswordCredentials("guest", "guest")
      );

      client.getParams().setAuthenticationPreemptive(true);
      */
      ApacheHttpClientExecutor executor = new ApacheHttpClientExecutor(client);

      String queueConfig = "<topic name=\"testTopic\"></topic>";
      ClientRequest create = executor.createRequest("http://localhost:8080/rest-messaging/topics");
      ClientResponse cRes = create.body("application/hornetq.jms.topic+xml", queueConfig).post();
      Assert.assertEquals(201, cRes.getStatus());

      Link topic = cRes.getLocation();
      ClientRequest request = topic.request();

      ClientResponse response = request.head();
      Assert.assertEquals(200, response.getStatus());
      Link sender = response.getHeaderAsLink("msg-create");
      Link subscriptions = response.getHeaderAsLink("msg-subscriptions");


      ClientResponse res = subscriptions.request().post();
      Assert.assertEquals(201, res.getStatus());
      Link sub1 = res.getLocation();
      Assert.assertNotNull(sub1);
      Link consumeNext1 = res.getHeaderAsLink("msg-consume-next");
      Assert.assertNotNull(consumeNext1);
      System.out.println("consumeNext1: " + consumeNext1);


      res = subscriptions.request().post();
      Assert.assertEquals(201, res.getStatus());
      Link sub2 = res.getLocation();
      Assert.assertNotNull(sub2);
      Link consumeNext2 = res.getHeaderAsLink("msg-consume-next");
      Assert.assertNotNull(consumeNext1);


      res = sender.request().body("text/plain", Integer.toString(1)).post();
      Assert.assertEquals(201, res.getStatus());
      res = sender.request().body("text/plain", Integer.toString(2)).post();
      Assert.assertEquals(201, res.getStatus());

      res = consumeNext1.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      consumeNext1 = res.getHeaderAsLink("msg-consume-next");

      res = consumeNext1.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      consumeNext1 = res.getHeaderAsLink("msg-consume-next");

      res = consumeNext2.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("1", res.getEntity(String.class));
      consumeNext2 = res.getHeaderAsLink("msg-consume-next");

      res = consumeNext2.request().post(String.class);
      Assert.assertEquals(200, res.getStatus());
      Assert.assertEquals("2", res.getEntity(String.class));
      consumeNext2 = res.getHeaderAsLink("msg-consume-next");
      Assert.assertEquals(204, sub1.request().delete().getStatus());
      Assert.assertEquals(204, sub2.request().delete().getStatus());
      Assert.assertEquals(204, topic.request().delete().getStatus());
   }
}
