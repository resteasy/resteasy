package org.jboss.resteasy.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface ContextInjector<WrappedType, UnwrappedType> {
   // FIXME: remove rawType and genericType?
   public WrappedType resolve(Class<? extends WrappedType> rawType, Type genericType, Annotation[] annotations);
}
