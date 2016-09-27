package org.jboss.resteasy.test.xxe.resource.xxeJettison;

import org.jboss.resteasy.annotations.providers.NoJackson;

import java.util.HashMap;

@NoJackson
public class MovieMap<K, V> extends HashMap<K, V> {
    private static final long serialVersionUID = -4947257779972800629L;

}
