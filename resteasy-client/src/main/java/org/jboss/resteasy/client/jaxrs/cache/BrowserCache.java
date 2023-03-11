package org.jboss.resteasy.client.jaxrs.cache;

import java.io.Serializable;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface BrowserCache {
    class Header implements Serializable {
        private static final long serialVersionUID = 4145981086454860081L;

        private String name;
        private String value;

        public Header(final String name, final String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    interface Entry {
        MultivaluedMap<String, String> getHeaders();

        boolean expired();

        Header[] getValidationHeaders();

        byte[] getCached();

        MediaType getMediaType();
    }

    Entry getAny(String key);

    Entry get(String key, MediaType accept);

    Entry put(String key, MediaType mediaType, MultivaluedMap<String, String> headers, byte[] cached, int expires, String etag,
            String lastModified);

    Entry remove(String key, MediaType type);

    void clear();

}
