package org.jboss.resteasy.test.client.proxy.resource;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Date;

@XmlRootElement
public class ProxyJaxbResourcePostMessage {
    private BigDecimal msgId;
    private Date createdDate;
    private String destinationId;
    private BigDecimal msgComp;
    private BigDecimal numLocTfmsProvided;
    private String sourceId;
    private String versionMajor;
    private String versionMinor;


    public BigDecimal getMsgId() {
        return msgId;
    }

    public void setMsgId(BigDecimal msgId) {
        this.msgId = msgId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public BigDecimal getMsgComp() {
        return msgComp;
    }

    public void setMsgComp(BigDecimal msgComp) {
        this.msgComp = msgComp;
    }

    public BigDecimal getNumLocTfmsProvided() {
        return numLocTfmsProvided;
    }

    public void setNumLocTfmsProvided(BigDecimal numLocTfmsProvided) {
        this.numLocTfmsProvided = numLocTfmsProvided;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getVersionMajor() {
        return versionMajor;
    }

    public void setVersionMajor(String versionMajor) {
        this.versionMajor = versionMajor;
    }

    public String getVersionMinor() {
        return versionMinor;
    }

    public void setVersionMinor(String versionMinor) {
        this.versionMinor = versionMinor;
    }
}
