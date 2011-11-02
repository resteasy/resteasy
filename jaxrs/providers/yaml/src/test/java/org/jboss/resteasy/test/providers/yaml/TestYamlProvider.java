package org.jboss.resteasy.test.providers.yaml;

import static org.jboss.resteasy.test.TestPortProvider.*;
import junit.framework.Assert;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

public class TestYamlProvider extends BaseResourceTest {

    private static final String TEST_URI = generateURL("/yaml");

    @Before
    public void setUp() {

        addPerRequestResource(YamlResource.class);

    }

    @Test
    public void testGet() throws Exception {

       ClientRequest request = new ClientRequest(TEST_URI);
       
       ClientResponse<String> response = request.get(String.class);
       
       Assert.assertEquals(200, response.getStatus());
       
       Assert.assertEquals("text/x-yaml", response.getHeaders().getFirst("Content-Type")); 
       
       String s = response.getEntity();       
       
       MyObject o1 = YamlResource.createMyObject();

       String s1 = new Yaml().dump(o1);
       
       Assert.assertEquals(s1, s);

    }

    @Test
    public void testPost() throws Exception {

       ClientRequest request = new ClientRequest(TEST_URI);
       
       MyObject o1 = YamlResource.createMyObject();

       String s1 = new Yaml().dump(o1); new String();
       
       request.body("text/x-yaml", s1);
       
       ClientResponse<String> response = request.post(String.class);
       
       Assert.assertEquals(200, response.getStatus());
       
       Assert.assertEquals("text/x-yaml", response.getHeaders().getFirst("Content-Type"));       
       
       Assert.assertEquals(s1, response.getEntity());       

    }

    @Test
    public void testBadPost() throws Exception {

       ClientRequest request = new ClientRequest(TEST_URI);
       
       request.body("text/x-yaml", "---! bad");
       
       ClientResponse<?> response = request.post();
       
       Assert.assertEquals(500, response.getStatus());
       
       response.releaseConnection();
       
    }

}
