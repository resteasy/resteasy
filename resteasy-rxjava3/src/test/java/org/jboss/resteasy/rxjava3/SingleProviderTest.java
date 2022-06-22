package org.jboss.resteasy.rxjava3;

import io.reactivex.rxjava3.core.Single;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SingleProviderTest
{
    private final SingleProvider provider = new SingleProvider();

    @Test
    public void testFromCompletionStage()
    {
        final CompletableFuture<Integer> cs = new CompletableFuture<>();
        cs.complete(1);
        final Single<?> single = provider.fromCompletionStage(cs);
        assertEquals(1, single.blockingGet());
    }

    @Test
    public void testFromCompletionStageNotDeferred() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final Single<?> single = provider.fromCompletionStage(someAsyncMethod(latch));

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertEquals("Hello!", single.blockingGet());
        assertEquals(0, latch.getCount());
    }

    @Test
    public void testFromCompletionStageDeferred() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final Single<?> single = provider.fromCompletionStage(() -> someAsyncMethod(latch));

        assertFalse(latch.await(1, TimeUnit.SECONDS));
        assertEquals("Hello!", single.blockingGet());
        assertEquals(0, latch.getCount());
    }

    private CompletableFuture<String> someAsyncMethod(final CountDownLatch latch) {
        latch.countDown();
        return CompletableFuture.completedFuture("Hello!");
    }

    @Test
    public void testToCompletionStageCase() throws Exception
    {
        final Object actual = provider.toCompletionStage(Single.just(1)).toCompletableFuture().get();
        assertEquals(1, actual);
    }
}
