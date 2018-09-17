package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Can inject lists.
 */
public class ListFormInjector extends AbstractCollectionFormInjector<List> {

    /**
     * Constructor.
     * @param collectionType collection type
     * @param genericType generic type
     * @param prefix prefix
     * @param factory provider factory
     */
    public ListFormInjector(Class collectionType, Class genericType, String prefix, ResteasyProviderFactory factory) {
        super(collectionType, genericType, prefix, Pattern.compile("^" + prefix + "\\[(\\d+)\\]"), factory);
    }

    /**
     * {@inheritDoc}
     *
     * @return ArrayList
     */
    @Override
    protected List createInstance(Class collectionType) {
        return new ArrayList();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(value = "unchecked")
    @Override
    protected void addTo(List collection, String key, Object value) {
        int index = Integer.parseInt(key);
        int size = collection.size();
        // in case the key doesn't come in sorted order
        if (collection.size() <= index) {
            for (int i = 0; i < index - size + 1; i++)
                collection.add(null);
        }
        collection.set(index, value);
    }
}