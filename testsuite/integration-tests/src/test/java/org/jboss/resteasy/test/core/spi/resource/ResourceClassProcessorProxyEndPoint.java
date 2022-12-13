package org.jboss.resteasy.test.core.spi.resource;

public class ResourceClassProcessorProxyEndPoint implements ResourceClassProcessorProxy {
    public String custom() {
        return "<a></a>";
    }
}
