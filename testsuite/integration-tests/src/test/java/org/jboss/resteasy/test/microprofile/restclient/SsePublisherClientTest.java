package org.jboss.resteasy.test.microprofile.restclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.microprofile.client.BuilderResolver;
import org.jboss.resteasy.test.microprofile.restclient.resource.MPSseResource;
import org.jboss.resteasy.test.providers.sse.ExecutorServletContextListener;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@RunWith(Arquillian.class)
@RunAsClient
public class SsePublisherClientTest {
    @ArquillianResource
    URL url;

    @Deployment
    public static Archive<?> deploy()
    {
        WebArchive war = TestUtil.prepareArchive(SsePublisherClientTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, MPSseResource.class, ExecutorServletContextListener.class);
    }

    private String generateURL(String path)
    {
       return PortProviderUtil.generateURL(path, SsePublisherClientTest.class.getSimpleName());
    }

    @Test
    public void testSseClient() throws Exception
    {
       RestClientBuilder builder = RestClientBuilder.newBuilder();
       RestClientBuilder resteasyBuilder = new BuilderResolver().newBuilder();
       assertEquals(resteasyBuilder.getClass(), builder.getClass());
       MPSseClient client = builder.baseUrl(new URL(generateURL(""))).build(MPSseClient.class);
       Publisher<String> publisher = client.getStrings();
       CountDownLatch resultsLatch = new CountDownLatch(5);

       final Set<String> eventStrings = new HashSet<>();
       StringSubscriber subscriber = new StringSubscriber(eventStrings, resultsLatch);
       publisher.subscribe(subscriber);
       Thread.sleep(1000);
       subscriber.request(5);
       assertTrue(resultsLatch.await(10, TimeUnit.SECONDS));
       //sent 12 items, expects these 10 values [msg4, msg3, msg2, msg1, msg8, msg11, msg7, msg10, msg9, msg0]
       assertTrue(eventStrings.size() == 10);
       //msg5 and msg6 are dropped
       assertFalse(eventStrings.contains("msg5") || eventStrings.contains("msg6"));
       assertNull(subscriber.throwable);
    }

    private static class StringSubscriber implements Subscriber<String>, AutoCloseable {

        final CountDownLatch eventLatch;
        Throwable throwable;
        Subscription subscription;
        Set<String> eventStrings;

        StringSubscriber(final Set<String> eventStrings, final CountDownLatch eventLatch) {
            this.eventLatch = eventLatch;
            this.eventStrings = eventStrings;
        }

        @Override
        public void onSubscribe(Subscription s) {
            subscription = s;
            request(5);
        }

        @Override
        public void onNext(String s) {
            eventStrings.add(s);
            eventLatch.countDown();
        }

        @Override
        public void onError(Throwable t) {
            throwable = t;
        }

        @Override
        public void onComplete() {
        }

        @Override
        public void close() throws Exception {
            subscription.cancel();
        }
        public void request(long requestedEvents) {
            subscription.request(requestedEvents);
        }
    }
}
