package org.jboss.resteasy.test.providers.multipart.resource;

import java.io.InputStream;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class HeaderFlushedOutputStreamBean {
    @FormParam("someBinary")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    private InputStream someBinary;

    public InputStream getSomeBinary() {
        return someBinary;
    }

    public void setSomeBinary(InputStream someBinary) {
        this.someBinary = someBinary;
    }
}
