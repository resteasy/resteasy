package org.jboss.resteasy.springmvc.resources;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.plugins.providers.ProviderHelper;
import org.jboss.resteasy.springmvc.tjws.TJWSEmbeddedSpringMVCServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class BasicSpringTest {

    TJWSEmbeddedSpringMVCServer server = null;
    long start;
    
    @Before
    public void before(){
        start = System.currentTimeMillis();
        server = new TJWSEmbeddedSpringMVCServer();
        server.setSpringConfigLocation("classpath:spring-test1.xml");
        server.setPort(8081);
        server.setRootResourcePath("");
        server.setSecurityDomain(null);
        server.start();
    }

    @After
    public void after(){
        server.stop();
        System.out.println(System.currentTimeMillis() - start);
    }
    
    @Test
    public void testBasic() throws HttpException, IOException{
        HttpClient client = new HttpClient();

        GetMethod method = new GetMethod("http://localhost:8081/basic");
        int status = client.executeMethod(method);
        String result = method.getResponseBodyAsString();
        Assert.assertEquals("test", result);
    }
}
