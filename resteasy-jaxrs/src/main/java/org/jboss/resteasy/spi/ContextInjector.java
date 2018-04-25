package org.jboss.resteasy.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface ContextInjector<T> {
   public T resolve(Class<? extends T> rawType, Type genericType, Annotation[] annotations);
}
