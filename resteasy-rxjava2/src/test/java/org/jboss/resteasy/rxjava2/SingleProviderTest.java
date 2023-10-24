package org.jboss.resteasy.rxjava2;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.reactivex.Single;

public class SingleProviderTest {
    private final SingleProvider provider = new SingleProvider();

    @Test
    public void testFromCompletionStage() {
        final CompletableFuture<Integer> cs = new CompletableFuture<>();
        cs.complete(1);
        final Single<?> single = provider.fromCompletionStage(cs);
        Assertions.assertEquals(1, single.blockingGet());
    }

    @Test
    public void testFromCompletionStageNotDeferred() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final Single<?> single = provider.fromCompletionStage(someAsyncMethod(latch));

        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals("Hello!", single.blockingGet());
        Assertions.assertEquals(0, latch.getCount());
    }

    @Test
    public void testFromCompletionStageDeferred() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final Single<?> single = provider.fromCompletionStage(() -> someAsyncMethod(latch));

        Assertions.assertFalse(latch.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals("Hello!", single.blockingGet());
        Assertions.assertEquals(0, latch.getCount());
    }

    private CompletableFuture<String> someAsyncMethod(final CountDownLatch latch) {
        latch.countDown();
        return CompletableFuture.completedFuture("Hello!");
    }

    @Test
    public void testToCompletionStageCase() throws Exception {
        final Object actual = provider.toCompletionStage(Single.just(1)).toCompletableFuture().get();
        Assertions.assertEquals(1, actual);
    }
}
