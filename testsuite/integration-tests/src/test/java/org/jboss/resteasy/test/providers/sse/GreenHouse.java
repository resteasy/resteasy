package org.jboss.resteasy.test.providers.sse;

public class GreenHouse
{
   private long timestamp;

   private int temperature;

   private int humidity;

   public GreenHouse()
   {
   }

   public GreenHouse(long timestamp, int temperature, int humidity)
   {
      this.timestamp = timestamp;
      this.temperature = temperature;
      this.humidity = humidity;
   }

   public long getTimestamp()
   {
      return timestamp;
   }

   public void setTimestamp(long timestamp)
   {
      this.timestamp = timestamp;
   }

   public int getTempe()
   {
      return temperature;
   }

   public void setTempe(int temperature)
   {
      this.temperature = temperature;
   }

   public int getHumid()
   {
      return humidity;
   }

   public void setHumid(int humidity)
   {
      this.humidity = humidity;
   }
}
