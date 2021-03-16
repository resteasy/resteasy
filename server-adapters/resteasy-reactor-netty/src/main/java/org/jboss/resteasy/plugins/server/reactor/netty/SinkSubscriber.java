package org.jboss.resteasy.plugins.server.reactor.netty;

import java.util.function.Consumer;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitFailureHandler;

public class SinkSubscriber {

    /**
     * See {@link reactor.core.publisher.Mono#subscribe(Consumer, Consumer, Runnable)}
     * @param sink - The sink that will consume signals emitted by the mono.
     * @param mono - The mono to subscribe on.
     */
    public static void subscribe(Sinks.Empty<Void> sink, final Mono<Void> mono) {
        mono.subscribe(
            v -> {},
            e -> sink.emitError(e, EmitFailureHandler.FAIL_FAST),
            /**
             * Ok not to check return value below because we're inside `subscribe` and the
             * source is a Publisher<Void>.<br>
             * See more in https://github.com/reactor/reactor-core/issues/2431
             */
            sink::tryEmitEmpty);
    }

}
