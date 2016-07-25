package org.jboss.resteasy.test.providers.multipart.resource;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;

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
