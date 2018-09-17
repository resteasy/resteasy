package org.jboss.resteasy.spi;

import org.reactivestreams.Publisher;

public interface AsyncStreamProvider<T> {
   Publisher toAsyncStream(T asyncResponse);
}
