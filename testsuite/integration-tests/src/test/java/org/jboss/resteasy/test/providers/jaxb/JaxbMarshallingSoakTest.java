package org.jboss.resteasy.test.providers.jaxb;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbMarshallingSoakAsyncService;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbMarshallingSoakItem;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.resteasy.utils.TimeoutUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails JAXB shouldn't have a concurrent problem and should unmarshall a Map property all the time
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
public class JaxbMarshallingSoakTest {
    private static Logger logger = Logger.getLogger(JaxbMarshallingSoakTest.class);
    public static int iterator = 500;
    public static AtomicInteger counter = new AtomicInteger();
    public static CountDownLatch latch;
    public static JAXBContext ctx;
    public static String itemString;
    int timeout = TimeoutUtil.adjust(60);

    static Client client;

    @BeforeEach
    public void init() {
        client = ((ResteasyClientBuilder) ClientBuilder.newBuilder())
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .connectionCheckoutTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .maxPooledPerRoute(500)
                .build();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(JaxbMarshallingSoakTest.class.getSimpleName());
        war.addClasses(JaxbMarshallingSoakItem.class, TestUtil.class, PortProviderUtil.class, TimeoutUtil.class);
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put("resteasy.async.job.service.enabled", "true");
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
            WebTarget target = client.target(generateURL("/mpac/add?oneway=true"));
            Response response = target.request().post(Entity.entity(itemString, "application/xml"));
            Assertions.assertEquals(HttpResponseCodes.SC_ACCEPTED, response.getStatus());
            response.close();
        }
        latch.await(10, TimeUnit.SECONDS);
        String message = String.format(new StringBuilder().append("RESTEasy should successes with marshalling %d times.")
                .append("But RESTEasy successes only %d times.").toString(), iterator, counter.get());
        Assertions.assertEquals(iterator, counter.get(), message);
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
                Assertions.fail(String.format("Clients did not terminate in %s seconds", timeout));
            }
        } catch (InterruptedException e) {
            Assertions.fail("ExecutorService[threadPool] was interrupted");
        }
        String message = String.format(new StringBuilder().append("RESTEasy should successes with marshalling %d times.")
                .append("But RESTEasy successes only %d times.").toString(), iterator, counter.get());
        Assertions.assertEquals(iterator, counter.get(), message);
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
