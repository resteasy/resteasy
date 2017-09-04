package org.jboss.resteasy.spi;

import org.reactivestreams.Publisher;

public interface AsyncStreamProvider<T> {
   public Publisher toAsyncStream(T asyncResponse);
}
