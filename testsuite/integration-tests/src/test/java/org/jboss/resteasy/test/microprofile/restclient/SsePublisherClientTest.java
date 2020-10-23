package org.jboss.resteasy.test.microprofile.restclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Arrays;
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
       CountDownLatch resultsLatch = new CountDownLatch(12);
       StringSubscriber subscriber = new StringSubscriber(12, resultsLatch);
       publisher.subscribe(subscriber);
       assertTrue(resultsLatch.await(30, TimeUnit.SECONDS));
       assertNull(subscriber.throwable);
    }

    private static class StringSubscriber implements Subscriber<String>, AutoCloseable {

        final Set<String> eventStrings = new HashSet<>();
        final CountDownLatch eventLatch;
        Throwable throwable;
        Subscription subscription;
        long requestedEvents;

        StringSubscriber(final long requestedEvents, final CountDownLatch eventLatch) {
            this.requestedEvents = requestedEvents;
            this.eventLatch = eventLatch;
        }

        @Override
        public void onSubscribe(Subscription s) {
            subscription = s;
            s.request(requestedEvents);
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
    }
}
