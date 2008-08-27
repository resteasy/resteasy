package org.jboss.resteasy.test.providers.yaml;

import junit.framework.Assert;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.ho.yaml.Yaml;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Before;
import org.junit.Test;

public class TestYamlProvider extends BaseResourceTest
{


   private static final String TEST_URI = "http://localhost:8081/yaml";


   HttpClient client;


   @Before
   public void setUp()
   {

      addPerRequestResource(YamlResource.class);

      client = new HttpClient();

   }


   @Test
   public void testGet() throws Exception
   {

      GetMethod get = new GetMethod(TEST_URI);

      MyObject o1 = YamlResource.createMyObject();

      String s1 = Yaml.dump(o1);

      client.executeMethod(get);

      Assert.assertEquals(200, get.getStatusCode());

      Assert.assertEquals("text/x-yaml", get.getResponseHeader("Content-Type").getValue());

      String s = get.getResponseBodyAsString();

      Assert.assertEquals(s1, s);

   }


   @Test
   public void testPost() throws Exception
   {

      PostMethod post = new PostMethod(TEST_URI);

      MyObject o1 = YamlResource.createMyObject();

      String s1 = Yaml.dump(o1);

      post.setRequestEntity(new StringRequestEntity(s1, "text/x-yaml", "utf-8"));

      client.executeMethod(post);

      Assert.assertEquals(200, post.getStatusCode());

      Assert.assertEquals("text/x-yaml", post.getResponseHeader("Content-Type").getValue());

      Assert.assertEquals(s1, post.getResponseBodyAsString());

   }


   @Test
   public void testBadPost() throws Exception
   {

      PostMethod post = new PostMethod(TEST_URI);

      post.setRequestEntity(new StringRequestEntity("---! bad", "text/x-yaml", "utf-8"));

      client.executeMethod(post);

      Assert.assertEquals(400, post.getStatusCode());

   }


}
