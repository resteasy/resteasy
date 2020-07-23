package org.jboss.resteasy.test.microprofile.restclient;

import io.reactivex.exceptions.Exceptions;
import io.reactivex.subscribers.DefaultSubscriber;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.microprofile.restclient.resource.WeatherEvent;
import org.jboss.resteasy.test.microprofile.restclient.resource.WeatherEventClient;
import org.jboss.resteasy.test.microprofile.restclient.resource.WeatherEventProvider;
import org.jboss.resteasy.test.microprofile.restclient.resource.WeatherEventProviderJSON;
import org.jboss.resteasy.test.microprofile.restclient.resource.WeatherEventService;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.reactivestreams.Publisher;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @tpSubChapter Microprofile-rest-client 2.0
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.6.0
 */
@RunWith(Arquillian.class)
@RunAsClient
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WeatherEventTest {

    private static WeatherEventClient weatherEventClient;
    private static CountDownLatch latch;
    private static AtomicInteger errors;

    private static final String WAR_SERVICE = WeatherEventTest.class.getSimpleName();

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(WAR_SERVICE);
        war.addClasses(WeatherEvent.class,
                WeatherEventProviderJSON.class,
                WeatherEventProvider.class,
                WeatherEventService.class);
        war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services, org.reactivestreams\n"));
        return TestUtil.finishContainerPrepare(war, null, null);
    }

    private static String generateURL(String path, String deployName) {
        return PortProviderUtil.generateURL(path, deployName);
    }

    //////////////////////////////////////////////////////////////////////////////
    @BeforeClass
    public static void beforeClass() throws Exception {
        RestClientBuilder builder = RestClientBuilder.newBuilder();
        weatherEventClient = builder.baseUri(URI.create(generateURL("", WAR_SERVICE)))
                .build(WeatherEventClient.class);
    }

    //////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    @Test
    public void testWeatherEvents() throws Exception {
        latch = new CountDownLatch(1);
        errors = new AtomicInteger(0);
        ArrayList<WeatherEvent> weatherEventList = new ArrayList<WeatherEvent>();
        List<WeatherEvent> xWeatherEventList =
                WeatherEventService.generatedWeatherEvents();

        Publisher<WeatherEvent> publisher = weatherEventClient.getEvents();
        publisher.subscribe(new DefaultSubscriber<WeatherEvent>() {
            public void onNext(WeatherEvent var1) {
                try {
                    weatherEventList.add(var1);
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    onError(e);
                }
            }

            @Override
            public void onError(Throwable var1) {
                errors.incrementAndGet();
            }

            @Override
            public void onComplete() {
                latch.countDown();
            }
        });

        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
        Assert.assertEquals(0, errors.get());
        for (int i = 0; i < weatherEventList.size(); i++) {
            Assert.assertTrue("Compare failed for weatherEventList item " + i,
                    weatherEventList.get(i).compare(xWeatherEventList.get(i)));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testWeatherEventsJson() throws Exception {
        latch = new CountDownLatch(1);
        errors = new AtomicInteger(0);
        ArrayList<WeatherEvent> weatherEventList = new ArrayList<WeatherEvent>();
        List<WeatherEvent> xWeatherEventList =
                WeatherEventService.generatedWeatherEvents();

        Publisher<WeatherEvent> publisher = weatherEventClient.getEventsJson();
        publisher.subscribe(new DefaultSubscriber<WeatherEvent>() {
            public void onNext(WeatherEvent var1) {
                try {
                    weatherEventList.add(var1);
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    onError(e);
                }
            }

            @Override
            public void onError(Throwable var1) {
                errors.incrementAndGet();
            }

            @Override
            public void onComplete() {
                latch.countDown();
            }
        });

        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
        Assert.assertEquals(0, errors.get());
        for (int i = 0; i < weatherEventList.size(); i++) {
            Assert.assertTrue("Compare failed for weatherEventList item " + i,
                    weatherEventList.get(i).compare(xWeatherEventList.get(i)));
        }
    }
}
