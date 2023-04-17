package org.jboss.resteasy.test.providers.multipart.resource;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class ProxyAttachment {

    @HeaderParam("X-Atlassian-Token")
    @PartType("text/plain")
    private String multipartHeader = "nocheck";

    @FormParam("file")
    @PartType("text/plain")
    private byte[] data;

    public String getMultipartHeader() {
        return multipartHeader;
    }

    public void setMultipartHeader(String multipartHeader) {
        this.multipartHeader = multipartHeader;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}
