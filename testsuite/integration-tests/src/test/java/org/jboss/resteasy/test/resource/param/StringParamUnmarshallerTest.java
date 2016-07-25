package org.jboss.resteasy.test.resource.param;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.annotations.StringParameterUnmarshallerBinder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.resource.param.resource.StringParamUnmarshallerDateFormatter;
import org.jboss.resteasy.test.resource.param.resource.StringParamUnmarshallerFruit;
import org.jboss.resteasy.test.resource.param.resource.StringParamUnmarshallerService;
import org.jboss.resteasy.test.resource.param.resource.StringParamUnmarshallerSport;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Invocation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Test for unmarshalling with string parameter. StringParameterUnmarshallerBinder annotation is used
 */
@RunWith(Arquillian.class)
@RunAsClient
public class StringParamUnmarshallerTest {
    @Retention(RetentionPolicy.RUNTIME)
    @StringParameterUnmarshallerBinder(StringParamUnmarshallerDateFormatter.class)
    public @interface StringParamUnmarshallerDateFormat {
        String value();
    }
    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(StringParamUnmarshallerTest.class.getSimpleName());
        war.addClass(StringParamUnmarshallerDateFormatter.class);
        war.addClass(StringParamUnmarshallerFruit.class);
        war.addClass(StringParamUnmarshallerSport.class);
        war.addClass(StringParamUnmarshallerDateFormat.class);
        return TestUtil.finishContainerPrepare(war, null, StringParamUnmarshallerService.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, StringParamUnmarshallerTest.class.getSimpleName());
    }


    @Test
    public void testDate() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        Invocation.Builder request = client.target(generateURL("/datetest/04-23-1977")).request();
        String date = request.get(String.class);
        Assert.assertTrue("Received wrong date", date.contains("Sat Apr 23 00:00:00"));
        Assert.assertTrue("Received wrong date", date.contains("1977"));
        client.close();
    }

    @Test
    public void testFruitAndSport() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        Invocation.Builder request = client.target(generateURL("/fromstring/ORANGE/football")).request();
        Assert.assertEquals("Received wrong response", "footballORANGE", request.get(String.class));
        client.close();
    }
}
