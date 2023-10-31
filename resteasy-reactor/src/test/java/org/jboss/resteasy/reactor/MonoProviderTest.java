package org.jboss.resteasy.reactor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Mono;

public class MonoProviderTest {
    private final MonoProvider provider = new MonoProvider();

    @Test
    public void testFromCompletionStage() {
        final CompletableFuture<Integer> cs = new CompletableFuture<>();
        cs.complete(1);
        final Mono<?> mono = provider.fromCompletionStage(cs);
        Assertions.assertEquals(1, mono.block());
    }

    @Test
    public void testFromCompletionStageNotDeferred() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final Mono<?> mono = provider.fromCompletionStage(someAsyncMethod(latch));

        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals("Hello!", mono.block());
        Assertions.assertEquals(0, latch.getCount());
    }

    @Test
    public void testFromCompletionStageDeferred() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final Mono<?> mono = provider.fromCompletionStage(() -> someAsyncMethod(latch));

        Assertions.assertFalse(latch.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals("Hello!", mono.block());
        Assertions.assertEquals(0, latch.getCount());
    }

    private CompletableFuture<String> someAsyncMethod(final CountDownLatch latch) {
        latch.countDown();
        return CompletableFuture.completedFuture("Hello!");
    }

    @Test
    public void testToCompletionStageCase() throws Exception {
        final Object actual = provider.toCompletionStage(Mono.just(1)).toCompletableFuture().get();
        Assertions.assertEquals(1, actual);
    }

    @Test
    public void testToCompletionStageNullCase() throws Exception {
        // Kind of a weird test, but added with code that fixed a hang.
        final CompletableFuture<Integer> cs = new CompletableFuture<>();
        cs.complete(null);
        final Mono<?> mono = Mono.fromCompletionStage(cs);
        final Object actual = provider.toCompletionStage(mono).toCompletableFuture().get();
        Assertions.assertNull(actual);
    }
}
