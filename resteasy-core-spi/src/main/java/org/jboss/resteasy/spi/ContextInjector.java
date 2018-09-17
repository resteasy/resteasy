package org.jboss.resteasy.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @param <WrappedType> A class that wraps a data type or data object (e.g. Single<Foo>)
 * @param <UnwrappedType> The data type or data object declared in the WrappedType (e.g. Foo)
 */
public interface ContextInjector<WrappedType, UnwrappedType> {
   // FIXME: remove rawType and genericType?

   /**
    * This interface allows users to create custom injectable asynchronous types.
    *
    * Asynch injection is only attempted at points where asynchronous injection is
    * permitted, such as on resource creation and resource method invocation. It
    * is not enabled at points where the API does not allow for suspending the
    * request
    *
    * @param rawType
    * @param genericType
    * @param annotations The annotation list is useful to parametrize the injection.
    * @return
    */
   WrappedType resolve(Class<? extends WrappedType> rawType, Type genericType, Annotation[] annotations);
}
