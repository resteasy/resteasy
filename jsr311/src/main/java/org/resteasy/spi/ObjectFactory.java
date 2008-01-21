package org.resteasy.spi;

/**
 * used by ResteasyProviderFactory for the createInstance() method
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ObjectFactory<T> {
    /**
     * Ascertain if the Provider supports a particular type.
     *
     * @param type the type that is to be supported.
     * @return true if the type is supported, otherwise false.
     */
    boolean supports(Class<?> type);

    T create(Class<T> type);
}
