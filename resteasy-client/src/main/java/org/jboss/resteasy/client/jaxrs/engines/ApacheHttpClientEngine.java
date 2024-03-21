package org.jboss.resteasy.client.jaxrs.engines;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.spi.config.SizeUnit;

/**
 *
 * @deprecated This will be removed in a future release as the underlying default implementation of the
 *             {@link org.jboss.resteasy.client.jaxrs.ClientHttpEngine} will be replaced.
 */
@Deprecated(forRemoval = true, since = "6.2")
public interface ApacheHttpClientEngine extends ClientHttpEngine {
    /**
     * Enumeration to represent memory units.
     *
     * @deprecated use {@link SizeUnit} or {@link org.jboss.resteasy.spi.config.Threshold}
     */
    @Deprecated
    enum MemoryUnit {
        /**
         * Bytes
         */
        BY,
        /**
         * Killo Bytes
         */
        KB,

        /**
         * Mega Bytes
         */
        MB,

        /**
         * Giga Bytes
         */
        GB;

        SizeUnit toSizeUnit() {
            switch (this) {
                case BY:
                    return SizeUnit.BYTE;
                case KB:
                    return SizeUnit.KILOBYTE;
                case MB:
                    return SizeUnit.MEGABYTE;
                case GB:
                    return SizeUnit.GIGABYTE;
            }
            return SizeUnit.BYTE;
        }

        static MemoryUnit of(final SizeUnit unit) {
            switch (unit) {
                case KILOBYTE:
                    return KB;
                case MEGABYTE:
                    return MB;
                case GIGABYTE:
                    return GB;
                default:
                    return BY;
            }
        }
    }

    static ApacheHttpClientEngine create() {
        return new ApacheHttpClient43Engine();
    }

    static ApacheHttpClientEngine create(CloseableHttpClient httpClient) {
        return new ApacheHttpClient43Engine(httpClient);
    }

    static ApacheHttpClientEngine create(HttpClient httpClient, boolean closeHttpClient) {
        return new ApacheHttpClient43Engine(httpClient, closeHttpClient);
    }
}
