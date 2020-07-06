package org.jboss.resteasy.test.microprofile.restclient;

import org.eclipse.microprofile.rest.client.ext.QueryParamStyle;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.microprofile.client.RestClientBuilderImpl;
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
 * @tpTestCaseDetails Show QueryParamStyle working.
 * @tpSince RESTEasy 4.6.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class QueryParamStyleTest {
    protected static final Logger LOG = Logger.getLogger(QueryParamStyleTest.class.getName());
    private static final String WAR_SERVICE = "queryParamStyle_service";

    @Deployment(name=WAR_SERVICE)
    public static Archive<?> serviceDeploy() {
        WebArchive war = TestUtil.prepareArchive(WAR_SERVICE);
        war.addClasses(QueryParamStyleService.class);
        return TestUtil.finishContainerPrepare(war, null, null);
    }

    static RestClientBuilderImpl builder;
    static List<String> argList = new ArrayList<>();

    @Before
    public void before() throws Exception {
        builder = new RestClientBuilderImpl();
        builder.baseUri(URI.create(generateURL("", WAR_SERVICE)));

        argList.clear();
        argList.add("client call");
        argList.add("hello");
        argList.add("three");
    }

    private static String generateURL(String path, String deployName) {
        return PortProviderUtil.generateURL(path, deployName);
    }

    /*
     * Use default format setting (i.e. QueryParamStyle.MULTI_PAIRS)
     */
    @Test
    public void defaultSetting() {

        QueryParamStyleServiceIntf serviceIntf = builder
                .build(QueryParamStyleServiceIntf.class);
        List<String> l = serviceIntf.getList(argList);

        Assert.assertEquals(4, l.size());
        Assert.assertEquals("theService reached", l.get(3));
    }

    /*
     * Use QueryParamStyle.MULTI_PAIRS
     */
    @Test
    public void multiPairs() {

        QueryParamStyleServiceIntf serviceIntf = builder
                .queryParamStyle(QueryParamStyle.MULTI_PAIRS)
                .build(QueryParamStyleServiceIntf.class);

        List<String> l = serviceIntf.getList(argList);
        Assert.assertEquals(4, l.size());
        Assert.assertEquals("theService reached", l.get(3));
    }

    /*
     * Use QueryParamStyle.COMMA_SEPARATED
     */
    @Test
    public void commaSeparated() {

        QueryParamStyleServiceIntf serviceIntf = builder
                .queryParamStyle(QueryParamStyle.COMMA_SEPARATED)
                .build(QueryParamStyleServiceIntf.class);

        List<String> l = serviceIntf.getList(argList);
        Assert.assertEquals(2, l.size());
        Assert.assertEquals("client call,hello,three", l.get(0));
    }

    /*
     * Use QueryParamStyle.ARRAY_PAIRS
     */
    @Test
    public void arraPairs() {

        QueryParamStyleServiceIntf serviceIntf = builder
                .queryParamStyle(QueryParamStyle.ARRAY_PAIRS)
                .build(QueryParamStyleServiceIntf.class);

        List<String> l = serviceIntf.getList(argList);
        Assert.assertEquals(1, l.size());
        Assert.assertEquals("theService reached", l.get(0));
    }
}
