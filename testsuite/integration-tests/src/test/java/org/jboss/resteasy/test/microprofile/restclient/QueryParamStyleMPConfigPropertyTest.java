package org.jboss.resteasy.test.microprofile.restclient;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.microprofile.restclient.resource.QueryParamStyleService;
import org.jboss.resteasy.test.microprofile.restclient.resource.QueryParamStyleServiceIntf;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @tpSubChapter MicroProfile rest client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Show using microprofile-config property, "/mp-rest/queryParamStyle" works.
 * @tpSince RESTEasy 4.6.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class QueryParamStyleMPConfigPropertyTest {
    protected static final Logger LOG = Logger.getLogger(QueryParamStyleMPConfigPropertyTest.class.getName());
    private static final String WAR_SERVICE = "queryParamStyle_MPConifg_service";

    @Deployment(name=WAR_SERVICE)
    public static Archive<?> serviceDeploy() {
        WebArchive war = TestUtil.prepareArchive(WAR_SERVICE);
        war.addClasses(QueryParamStyleService.class);
        return TestUtil.finishContainerPrepare(war, null, null);
    }

    static RestClientBuilder builder;
    static List<String> argList = new ArrayList<>();

    @Before
    public void before() throws Exception {
        builder = RestClientBuilder.newBuilder();
        builder.baseUri(URI.create(generateURL("", WAR_SERVICE)));

        argList.clear();
        argList.add("client call");
        argList.add("hello");
        argList.add("three");
    }

    private static String generateURL(String path, String deployName) {
        return PortProviderUtil.generateURL(path, deployName);
    }

    @Test
    public void commaSeparated() {
        String key = QueryParamStyleServiceIntf.class.getCanonicalName()+"/mp-rest/queryParamStyle";
        System.setProperty(key, "COMMA_sePARated");

        QueryParamStyleServiceIntf serviceIntf = builder
                .build(QueryParamStyleServiceIntf.class);

        List<String> l = serviceIntf.getList(argList);
        Assert.assertEquals(2, l.size());
        Assert.assertEquals("client call,hello,three", l.get(0));
        System.clearProperty(key);
    }

    @Test
    public void arraPairs() {
        String key = "qParamS"+"/mp-rest/queryParamStyle";
        System.setProperty(key, "arraY_Pairs");

        QueryParamStyleServiceIntf serviceIntf = builder
                .build(QueryParamStyleServiceIntf.class);

        List<String> l = serviceIntf.getList(argList);
        Assert.assertEquals(1, l.size());
        Assert.assertEquals("theService reached", l.get(0));
        System.clearProperty(key);
    }
}
