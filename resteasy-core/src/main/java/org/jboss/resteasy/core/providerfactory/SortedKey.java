package org.jboss.resteasy.core.providerfactory;

import jakarta.ws.rs.Priorities;

import org.jboss.resteasy.core.MediaTypeMap;
import org.jboss.resteasy.spi.model.AbstractEntityProvider;
import org.jboss.resteasy.spi.model.EntityProvider;
import org.jboss.resteasy.spi.util.Types;

/**
 * Allow us to sort message body implementations that are more specific for their types
 * i.e. MessageBodyWriter&#x3C;Object&#x3E; is less specific than MessageBodyWriter&#x3C;String&#x3E;.
 * <p>
 * This helps out a lot when the desired media type is a wildcard and to weed out all the possible
 * default mappings.
 */
// TODO (jrp) it would be nice to deprecate this. It would be better to use the EntityProvider and explicit implementations for the types
public class SortedKey<T> extends AbstractEntityProvider<T>
        implements Comparable<SortedKey<T>>, MediaTypeMap.Typed, EntityProvider<T> {

    public SortedKey(final Class<?> intf, final T reader, final Class<?> readerClass, final int priority,
            final boolean isBuiltin) {
        super(reader, resolveGenericType(readerClass, intf), priority, isBuiltin);
    }

    public SortedKey(final Class<?> intf, final T reader, final Class<?> readerClass, final boolean isBuiltin) {
        this(intf, reader, readerClass, Priorities.USER, isBuiltin);
    }

    public SortedKey(final Class<?> intf, final T reader, final Class<?> readerClass) {
        this(intf, reader, readerClass, Priorities.USER, false);
    }

    /**
     * Direct populate
     *
     * @param obj
     * @param isBuiltin
     * @param template  template class of component type
     * @param priority
     */
    public SortedKey(final T obj, final boolean isBuiltin, final Class<?> template, final int priority) {
        super(obj, template, priority, isBuiltin);
    }

    public int compareTo(SortedKey<T> tMessageBodyKey) {
        // Sort user provider before builtins
        if (this == tMessageBodyKey)
            return 0;
        if (isBuiltIn() == tMessageBodyKey.isBuiltIn()) {
            if (this.priority() < tMessageBodyKey.priority()) {
                return -1;
            }
            if (this.priority() == tMessageBodyKey.priority()) {
                return 0;
            }
            if (this.priority() > tMessageBodyKey.priority()) {
                return 1;
            }
        }
        if (isBuiltIn())
            return 1;
        else
            return -1;
    }

    public Class<?> getType() {
        return providerType();
    }

    public T getObj() {
        return provider();
    }

    public int getPriority() {
        return priority();
    }

    private static Class<?> resolveGenericType(final Class<?> type, final Class<?> intf) {
        // check the super class for the generic type 1st
        final Class<?> t = Types.getTemplateParameterOfInterface(type, intf);
        return (t != null) ? t : Object.class;
    }
}
