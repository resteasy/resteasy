package org.jboss.resteasy.test.providers.multipart.resource;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ContextProvidersXop {
    @XmlMimeType(MediaType.APPLICATION_OCTET_STREAM)
    private byte[] bytes;

    public ContextProvidersXop(final byte[] bytes) {
        this.bytes = bytes;
    }

    public ContextProvidersXop() {
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
