package org.jboss.resteasy.test.resource.basic.resource;

import org.jboss.resteasy.core.MediaTypeMap;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.ext.MessageBodyReader;

public class SpecialResourceSubFactory extends ResteasyProviderFactory {
    public MediaTypeMap<SortedKey<MessageBodyReader>> getMBRMap() {
        return serverMessageBodyReaders;
    }
}
