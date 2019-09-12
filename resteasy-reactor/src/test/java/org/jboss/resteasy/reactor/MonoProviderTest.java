package org.jboss.resteasy.reactor;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import reactor.core.publisher.Mono;

public class MonoProviderTest
{
    private final MonoProvider provider = new MonoProvider();

    @Test
    public void testFromCompletionStage()
    {
        final CompletableFuture<Integer> cs = new CompletableFuture<>();
        cs.complete(1);
        final Mono<?> mono = provider.fromCompletionStage(cs);
        assertEquals(1, mono.block());
    }

    @Test
    public void testToCompletionStageCase() throws Exception
    {
        final Object actual = provider.toCompletionStage(Mono.just(1)).toCompletableFuture().get();
        assertEquals(1, actual);
    }

    @Test
    public void testToCompletionStageNullCase() throws Exception
    {
        // Kind of a weird test, but added with code that fixed a hang.
        final CompletableFuture<Integer> cs = new CompletableFuture<>();
        cs.complete(null);
        final Mono<?> mono = Mono.fromCompletionStage(cs);
        final Object actual = provider.toCompletionStage(mono).toCompletableFuture().get();
        assertNull(actual);
    }
}
