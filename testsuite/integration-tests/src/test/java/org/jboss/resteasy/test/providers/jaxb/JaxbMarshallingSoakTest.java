package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbMarshallingSoakAsyncService;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbMarshallingSoakItem;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.resteasy.utils.TimeoutUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.lang.reflect.ReflectPermission;
import java.net.SocketPermission;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyPermission;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.LoggingPermission;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails JAXB shouldn't have a concurrent problem and should unmarshall a Map property all the time
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
public class JaxbMarshallingSoakTest {
    private static Logger logger = Logger.getLogger(JaxbMarshallingSoakTest.class);
    public static int iterator = 500;
    public static AtomicInteger counter = new AtomicInteger();
    public static CountDownLatch latch;
    public static JAXBContext ctx;
    public static String itemString;
    int timeout = TimeoutUtil.adjust(60);

    static ResteasyClient client;

    @Before
    public void init() {
        client = new ResteasyClientBuilder()
                .establishConnectionTimeout(5000, TimeUnit.MILLISECONDS)
                .connectionCheckoutTimeout(5000, TimeUnit.MILLISECONDS)
                .socketTimeout(5000, TimeUnit.MILLISECONDS)
                .maxPooledPerRoute(500)
                .build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war =  TestUtil.prepareArchive(JaxbMarshallingSoakTest.class.getSimpleName());
        war.addClasses(JaxbMarshallingSoakItem.class, TestUtil.class, PortProviderUtil.class, TimeoutUtil.class);
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put("resteasy.async.job.service.enabled", "true");
        // Arquillian in the deployment use if TimeoutUtil in the deployment
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new RuntimePermission("getClassLoader"),
                new RuntimePermission("modifyThread"),
                new ReflectPermission("suppressAccessChecks"),
                new LoggingPermission("control", ""),
                new PropertyPermission("arquillian.*", "read"),
                new PropertyPermission("ipv6", "read"),
                new PropertyPermission("node", "read"),
                new PropertyPermission("org.jboss.resteasy.port", "read"),
                new PropertyPermission("ts.timeout.factor", "read"),
                new RuntimePermission("accessDeclaredMembers"),
                new RuntimePermission("getenv.RESTEASY_PORT"),
                new SocketPermission(PortProviderUtil.getHost(), "connect,resolve")
        ), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, contextParam, JaxbMarshallingSoakAsyncService.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, JaxbMarshallingSoakTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test with client.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void basicTest() throws Exception {
        latch = new CountDownLatch(iterator);
        ctx = JAXBContext.newInstance(JaxbMarshallingSoakItem.class);
        counter.set(0);
        itemString = setString();
        logger.info(String.format("Request: %s", itemString));
        for (int i = 0; i < iterator; i++) {
            ResteasyWebTarget target = client.target(generateURL("/mpac/add?oneway=true"));
            Response response = target.request().post(Entity.entity(itemString, "application/xml"));
            Assert.assertEquals(HttpResponseCodes.SC_ACCEPTED, response.getStatus());
            response.close();
        }
        latch.await(10, TimeUnit.SECONDS);
        String message = String.format(new StringBuilder().append("RESTEasy should successes with marshalling %d times.")
                .append("But RESTEasy successes only %d times.").toString(), iterator, counter.get());
        Assert.assertEquals(message, iterator, counter.get());
    }

    /**
     * @tpTestDetails Server test.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void compare() throws Exception {
        itemString = setString();
        ctx = JAXBContext.newInstance(JaxbMarshallingSoakItem.class);

        counter.set(0);

        Thread[] threads = new Thread[iterator];
        for (int i = 0; i < iterator; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    byte[] bytes = itemString.getBytes();
                    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                    JaxbMarshallingSoakItem item = null;
                    try {
                        item = (JaxbMarshallingSoakItem) ctx.createUnmarshaller().unmarshal(bais);
                    } catch (JAXBException e) {
                        throw new RuntimeException(e);
                    }
                    item.toString();
                    counter.incrementAndGet();

                }
            };
            threads[i] = thread;
        }
        ExecutorService threadPool = Executors.newFixedThreadPool(100);
        for (int i = 0; i < iterator; i++) {
            threadPool.submit(threads[i]);
        }
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(timeout, TimeUnit.SECONDS)) {
                Assert.fail(String.format("Clients did not terminate in %s seconds", timeout));
            }
        } catch (InterruptedException e) {
            Assert.fail("ExecutorService[threadPool] was interrupted");
        }
        String message = String.format(new StringBuilder().append("RESTEasy should successes with marshalling %d times.")
                .append("But RESTEasy successes only %d times.").toString(), iterator, counter.get());
        Assert.assertEquals(message, iterator, counter.get());
    }

    private String setString() {
        StringBuffer sbuffer = new StringBuffer();
        sbuffer.append("<item>");
        sbuffer.append("<price>1000</price>");
        sbuffer.append("<description>Allah Hafiz</description>");
        sbuffer.append("<requestID>");
        sbuffer.append("i");
        sbuffer.append("</requestID>");

        sbuffer.append("<dummy1>DUMMY1</dummy1>");
        sbuffer.append("<dummy2>DUMMY2</dummy2>");
        sbuffer.append("<dummy3>DUMMY3</dummy3>");
        sbuffer.append("<dummy4>DUMMY4</dummy4>");
        sbuffer.append("<dummy5>DUMMY5</dummy5>");
        sbuffer.append("<dummy6>DUMMY6</dummy6>");
        sbuffer.append("<dummy7>DUMMY7</dummy7>");
        sbuffer.append("<dummy8>DUMMY8</dummy8>");

        sbuffer.append("<harness>");
        sbuffer.append("<entry>");
        sbuffer.append("<key>P_REGIONCD</key>");
        sbuffer.append("<value>325</value>");
        sbuffer.append("</entry>");
        sbuffer.append("<entry>");
        sbuffer.append("<key>P_COUNTYMUN</key>");
        sbuffer.append("<value>447</value>");
        sbuffer.append("</entry>");
        sbuffer.append("<entry>f");
        sbuffer.append("<key>p_SrcView</key>");
        sbuffer.append("<value>C</value>");
        sbuffer.append("</entry>");
        sbuffer.append("</harness>");

        sbuffer.append("</item>");
        return sbuffer.toString();
    }
}


