package org.jboss.resteasy.grpc.sse.runtime;

import org.jboss.resteasy.plugins.providers.sse.InboundSseEventImpl;

public class SseEvent {

   private String  comment;
   private String  id;
   private String  name;
   private byte[]  data;
   private long    reconnectDelay;

   public SseEvent() {}

   public SseEvent(final InboundSseEventImpl inboundSseEvent) {
      setComment(inboundSseEvent.getComment());
      setData(inboundSseEvent.getRawData());
      setId(inboundSseEvent.getId());
      setName(inboundSseEvent.getName());
      setReconnectDelay(inboundSseEvent.getReconnectDelay());
   }

   public String getComment()
   {
      return comment;
   }
   public void setComment(String comment)
   {
      this.comment = comment;
   }
   public String getId()
   {
      return id;
   }
   public void setId(String id)
   {
      this.id = id;
   }
   public String getName()
   {
      return name;
   }
   public void setName(String name)
   {
      this.name = name;
   }
   public byte[] getData() {
      return data;
   }
   public void setData(byte[] data)
   {
      this.data = data;
   }
   public long getReconnectDelay()
   {
      return reconnectDelay;
   }
   public void setReconnectDelay(long reconnectDelay)
   {
      this.reconnectDelay = reconnectDelay;
   }
   public boolean isReconnectDelaySet() {
      return reconnectDelay > -1;
   }
}
