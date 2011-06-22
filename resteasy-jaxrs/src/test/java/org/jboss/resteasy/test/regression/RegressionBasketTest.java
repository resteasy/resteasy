package org.jboss.resteasy.test.regression;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.InputStream;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * A basket of JIRA regression tests
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RegressionBasketTest extends BaseResourceTest
{
   @Path("/inputstream")
   public static class MyTest
   {
      @POST
      @Path("/test/{type}")
      public void test(InputStream is, @PathParam("type") final String type)
      {

      }
   }

   @BeforeClass
   public static void setup() throws Exception
   {
      addPerRequestResource(MyTest.class);
   }

   @Test
   public void test534() throws Exception
   {
      HttpClient client = new HttpClient();
      PostMethod post = new PostMethod(generateURL("/inputstream/test/json"));
      ByteArrayRequestEntity entity = new ByteArrayRequestEntity("hello world".getBytes(), null);
      post.setRequestEntity(entity);
      int status = client.executeMethod(post);
      Assert.assertEquals(204, status);
      post.releaseConnection();
   }
}
