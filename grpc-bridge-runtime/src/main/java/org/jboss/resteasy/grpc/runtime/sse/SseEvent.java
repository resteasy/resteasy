package org.jboss.resteasy.grpc.runtime.sse;

import com.google.protobuf.Any;

public class SseEvent {

    private String comment;
    private String id;
    private String name;
    private Any data;
    private long reconnectDelay;

    public SseEvent() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Any getData() {
        return data;
    }

    public void setData(Any data) {
        this.data = data;
    }

    public long getReconnectDelay() {
        return reconnectDelay;
    }

    public void setReconnectDelay(long reconnectDelay) {
        this.reconnectDelay = reconnectDelay;
    }

    public boolean isReconnectDelaySet() {
        return reconnectDelay > -1;
    }
}
