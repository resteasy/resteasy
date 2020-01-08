package org.jboss.resteasy.microprofile.client;

import org.eclipse.microprofile.rest.client.spi.RestClientListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;


public class RestClientListeners {

    private RestClientListeners() {
    }

    private static final Collection<RestClientListener> listeners;

    static {
        listeners = loadListeners();
    }

    private static List<RestClientListener> loadListeners() {
        List<RestClientListener> listeners = new ArrayList<>();
        ServiceLoader.load(RestClientListener.class)
                .forEach(listeners::add);
        Collections.unmodifiableCollection(listeners);
        return listeners;
    }

    public static Collection<RestClientListener> get() {
        return listeners;
    }
}
